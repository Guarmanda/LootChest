package eu.decentholo.holograms.api.holograms;


import eu.decentholo.holograms.api.utils.scheduler.S;
import eu.decentholo.holograms.api.utils.tick.Ticked;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a manager that handles all holograms. It is responsible for
 * loading, saving, creating, deleting, and updating holograms.
 */
public class HologramManager extends Ticked {


    private final Map<String, Hologram> hologramMap = new ConcurrentHashMap<>();
    private final Set<HologramLine> temporaryLines = ConcurrentHashMap.newKeySet();



    public HologramManager() {
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

    public void onQuit(@NonNull Player player) {
        Hologram.getCachedHolograms().forEach(hologram -> hologram.onQuit(player));
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
     * Remove hologram by name.
     *
     * @param name Name of the hologram.
     */
    public void removeHologram(@NonNull String name) {
        hologramMap.remove(name);
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


}
