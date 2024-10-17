package eu.decentholo.holograms.api.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import eu.decentholo.holograms.api.DecentHolograms;
import eu.decentholo.holograms.api.utils.scheduler.S;

public class PlayerListener implements Listener {

    private final DecentHolograms decentHolograms;

    public PlayerListener(DecentHolograms decentHolograms) {
        this.decentHolograms = decentHolograms;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        S.async(() -> decentHolograms.getHologramManager().updateVisibility(player));

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        S.async(() -> decentHolograms.getHologramManager().onQuit(player));

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        // TODO: All holograms (and entities) get hidden on the client, when the client
        //  teleports or respawns. This only seems to be happening on some client versions
        //  so we need to find which versions are affected and only re-show the holograms
        //  to those clients (or on those server versions).
        //  -
        //  For now, this only causes visual glitches where even if a player gets teleported
        //  by a fraction of a block, the holograms still disappear and reappear for them.
        //  -
        //  tl:dr Figure out which versions need this.
        S.async(() -> decentHolograms.getHologramManager().hideAll(player));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        S.async(() -> decentHolograms.getHologramManager().hideAll(player));
    }

}
