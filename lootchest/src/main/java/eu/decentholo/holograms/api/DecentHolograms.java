package eu.decentholo.holograms.api;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import eu.decentholo.holograms.api.holograms.Hologram;
import eu.decentholo.holograms.api.holograms.HologramManager;
import eu.decentholo.holograms.api.listeners.PlayerListener;
import eu.decentholo.holograms.api.listeners.WorldListener;
import eu.decentholo.holograms.api.nms.NMS;
import eu.decentholo.holograms.api.utils.reflect.Version;
import eu.decentholo.holograms.api.utils.tick.Ticker;
import fr.black_eyes.lootchest.Main;

/**
 * This is the main class of DecentHolograms. It contains all the methods
 * and fields that are used to manage DecentHolograms. You can get the instance
 * of this class by using {@link DecentHologramsAPI#get()}.
 *
 * @author d0by
 * @see DecentHologramsAPI
 */
@Getter
public final class DecentHolograms {

    private final JavaPlugin plugin;
    private HologramManager hologramManager;


    private Ticker ticker;

    /*
     *	Constructors
     */

    DecentHolograms(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /*
     *	General Methods
     */

    void load() {
        // Check if NMS version is supported
        if (Version.CURRENT == null) {
            Main.getInstance().getLogger().warning("Unsupported server version: " +Bukkit.getServer().getVersion());
            Main.getInstance().getLogger().warning("Plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    void enable() {
        NMS.init();

        this.ticker = new Ticker();
        this.hologramManager = new HologramManager();


        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this.plugin);
        pm.registerEvents(new WorldListener(this), this.plugin);
    }

    void disable() {
        this.hologramManager.destroy();
        this.ticker.destroy();

        for (Hologram hologram : Hologram.getCachedHolograms()) {
            hologram.destroy();
        }
    }


}
