package eu.decentholo.holograms.api.utils.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitTask;

import eu.decentholo.holograms.api.DecentHolograms;
import eu.decentholo.holograms.api.DecentHologramsAPI;
import eu.decentholo.holograms.api.utils.DExecutor;

public class S {

    private static final DecentHolograms DECENT_HOLOGRAMS = DecentHologramsAPI.get();

    public static void stopTask(int id) {
        Bukkit.getScheduler().cancelTask(id);
    }

    public static BukkitTask sync(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(DECENT_HOLOGRAMS.getPlugin(), runnable, delay);
    }

    public static void async(Runnable runnable) {
        try {
            Bukkit.getScheduler().runTaskAsynchronously(DECENT_HOLOGRAMS.getPlugin(), runnable);
        } catch (IllegalPluginAccessException e) {
            DExecutor.execute(runnable);
        }
    }

    public static BukkitTask asyncTask(Runnable runnable, long interval, long delay) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(DECENT_HOLOGRAMS.getPlugin(), runnable, delay, interval);
    }

    private S() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
