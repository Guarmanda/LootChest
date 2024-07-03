package eu.decentholo.holograms.api.holograms;

import eu.decentholo.holograms.api.DecentHolograms;


import eu.decentholo.holograms.api.utils.scheduler.S;
import eu.decentholo.holograms.api.utils.tick.Ticked;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a manager that handles all holograms. It is responsible for
 * loading, saving, creating, deleting, and updating holograms.
 */
public class HologramManager extends Ticked {


    private final Map<String, Hologram> hologramMap = new ConcurrentHashMap<>();
    private final Map<UUID, Long> clickCooldowns = new ConcurrentHashMap<>();
    private final Set<HologramLine> temporaryLines = ConcurrentHashMap.newKeySet();

    /**
     * Map of holograms to load when their respective world loads.
     * <p>
     * There were issues with world management plugins loading worlds
     * after holograms. Due to that, holograms in these worlds were skipped
     * as we can't load holograms, that don't have their world all loaded.
     * <p>
     * Key is the name of the world, and Value is a set of file names
     * of all holograms, that couldn't be loaded due to this world problem.
     *
     * @since 2.7.4
     */
    private final Map<String, Set<String>> toLoad = new ConcurrentHashMap<>();

    public HologramManager(DecentHolograms decentHolograms) {
        super(20L);
        this.register();

        S.async(this::reload); // Reload when the worlds are ready
    }

    @Override
    public synchronized void tick() {
        for (Hologram hologram : Hologram.getCachedHolograms()) {
            if (hologram.isEnabled()) {
                updateVisibility(hologram);
            }
        }
    }

    public void updateVisibility(@NonNull Hologram hologram) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateVisibility(player, hologram);
        }
    }

    public void updateVisibility(@NonNull Player player) {
        for (Hologram hologram : Hologram.getCachedHolograms()) {
            updateVisibility(player, hologram);
        }
    }

    public void updateVisibility(@NonNull Player player, @NonNull Hologram hologram) {
        if (hologram.isDisabled()) {
            return;
        }

        // Determine the player's display state of this hologram.
        if (hologram.isHideState(player) || (!hologram.isDefaultVisibleState() && !hologram.isShowState(player))) {
            if (hologram.isVisible(player)) {
                hologram.hide(player);
            }
            return;
        }

        if (!hologram.isVisible(player) && hologram.isInDisplayRange(player)) {
            hologram.show(player, hologram.getPlayerPage(player));
        } else if (hologram.isVisible(player) && !( hologram.isInDisplayRange(player))) {
            hologram.hide(player);
        }
    }

    /**
     * Spawn a temporary line going to disappear after the given duration.
     *
     * @param location Location of the line.
     * @param content  Content of the line.
     * @param duration Duration to disappear after. (In ticks)
     * @return The Hologram Line.
     */
    public HologramLine spawnTemporaryHologramLine(@NonNull Location location, String content, long duration) {
        HologramLine line = new HologramLine(null, location, content);
        temporaryLines.add(line);
        line.show();
        S.async(() -> {
            line.destroy();
            temporaryLines.remove(line);
        }, duration);
        return line;
    }

   

    public void onQuit(@NonNull Player player) {
        Hologram.getCachedHolograms().forEach(hologram -> hologram.onQuit(player));
        clickCooldowns.remove(player.getUniqueId());
    }

    /**
     * Reload this manager and all the holograms.
     */
    public synchronized void reload() {
        this.destroy();
    }

    /**
     * Destroy this manager and all the holograms.
     */
    public synchronized void destroy() {
        // Destroy registered holograms
        for (Hologram hologram : getHolograms()) {
            hologram.destroy();
        }
        hologramMap.clear();

        // Destroy temporary lines
        for (HologramLine line : temporaryLines) {
            line.destroy();
        }
        temporaryLines.clear();

        clickCooldowns.clear();
    }

    /**
     * Show all registered holograms for the given player.
     *
     * @param player Given player.
     */
    public void showAll(@NonNull Player player) {
        for (Hologram hologram : getHolograms()) {
            hologram.show(player, hologram.getPlayerPage(player));
        }
        for (HologramLine line : temporaryLines) {
            line.show(player);
        }
    }

    /**
     * Hide all registered holograms for the given player.
     *
     * @param player Given player.
     */
    public void hideAll(@NonNull Player player) {
        for (Hologram hologram : getHolograms()) {
            hologram.hide(player);
        }
        for (HologramLine line : temporaryLines) {
            line.hide(player);
        }
    }

    /**
     * Check whether a hologram with the given name is registered in this manager.
     *
     * @param name Name of the hologram.
     * @return Boolean whether a hologram with the given name is registered in this manager.
     */
    public boolean containsHologram(@NonNull String name) {
        return hologramMap.containsKey(name);
    }

    /**
     * Register a new hologram.
     *
     * @param hologram New hologram.
     */
    public void registerHologram(@NonNull Hologram hologram) {
        hologramMap.put(hologram.getName(), hologram);
    }

    /**
     * Get hologram by name.
     *
     * @param name Name of the hologram.
     * @return The hologram or null if it wasn't found.
     */
    public Hologram getHologram(@NonNull String name) {
        return hologramMap.get(name);
    }

    /**
     * Remove hologram by name.
     *
     * @param name Name of the hologram.
     * @return The hologram or null if it wasn't found.
     */
    public Hologram removeHologram(@NonNull String name) {
        return hologramMap.remove(name);
    }

    /**
     * Get the names of all registered holograms.
     *
     * @return Set of the names of all registered holograms.
     */
    public Set<String> getHologramNames() {
        return hologramMap.keySet();
    }

    /**
     * Get all registered holograms.
     *
     * @return Collection of all registered holograms.
     */
    @NonNull
    public Collection<Hologram> getHolograms() {
        return hologramMap.values();
    }

    @NonNull
    public Map<String, Set<String>> getToLoad() {
        return toLoad;
    }

}
