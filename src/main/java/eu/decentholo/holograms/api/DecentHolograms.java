package eu.decentholo.holograms.api;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;

import eu.decentholo.holograms.api.holograms.Hologram;
import eu.decentholo.holograms.api.holograms.HologramManager;
import eu.decentholo.holograms.api.listeners.PlayerListener;
import eu.decentholo.holograms.api.listeners.WorldListener;
import eu.decentholo.holograms.api.nms.NMS;
import eu.decentholo.holograms.api.utils.BungeeUtils;
import eu.decentholo.holograms.api.utils.DExecutor;
import eu.decentholo.holograms.api.utils.Log;
import eu.decentholo.holograms.api.utils.reflect.Version;
import eu.decentholo.holograms.api.utils.tick.Ticker;

import java.io.File;
import java.util.logging.Logger;

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
    private boolean updateAvailable;

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
            Log.error("Unsupported server version: %s", Bukkit.getServer().getVersion());
            Log.error("Plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    void enable() {
        NMS.init();

        DExecutor.init(3);

        this.ticker = new Ticker();
        this.hologramManager = new HologramManager(this);


        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this.plugin);
        pm.registerEvents(new WorldListener(this), this.plugin);

        BungeeUtils.init();
    }

    void disable() {
        this.hologramManager.destroy();
        this.ticker.destroy();

        for (Hologram hologram : Hologram.getCachedHolograms()) {
            hologram.destroy();
        }

        BungeeUtils.destroy();
        DExecutor.shutdownNow();
    }

    /**
     * Reload the plugin, this method also calls the reload event.
     *
     * @see DecentHologramsReloadEvent
     */
    public void reload() {

        this.hologramManager.reload();

  
    }

    @Contract(pure = true)
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Contract(pure = true)
    public Logger getLogger() {
        return plugin.getLogger();
    }

}
