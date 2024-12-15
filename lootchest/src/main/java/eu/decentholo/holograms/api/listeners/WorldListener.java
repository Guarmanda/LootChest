package eu.decentholo.holograms.api.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import eu.decentholo.holograms.api.DecentHolograms;
import eu.decentholo.holograms.api.holograms.DisableCause;
import eu.decentholo.holograms.api.holograms.Hologram;
import eu.decentholo.holograms.api.holograms.HologramManager;
import eu.decentholo.holograms.api.utils.scheduler.S;


public class WorldListener implements Listener {

    private final DecentHolograms decentHolograms;

    public WorldListener(DecentHolograms decentHolograms) {
        this.decentHolograms = decentHolograms;
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        HologramManager hologramManager = decentHolograms.getHologramManager();
        World world = event.getWorld();

        S.async(() -> hologramManager.getHolograms().stream()
                .filter(Hologram::isEnabled)
                .filter(hologram -> hologram.getLocation().getWorld().equals(world))
                .forEach(hologram -> hologram.disable(DisableCause.WORLD_UNLOAD)));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        HologramManager hologramManager = decentHolograms.getHologramManager();
        World world = event.getWorld();

        S.async(() -> {
           
            hologramManager.getHolograms().stream()
                    .filter(hologram -> !hologram.isEnabled())
                    .filter(hologram -> hologram.getLocation().getWorld().equals(world))
                    .filter(hologram -> hologram.getDisableCause().equals(DisableCause.WORLD_UNLOAD))
                    .forEach(Hologram::enable);
        });
    }
}
