package eu.decentholo.holograms.api.utils.event;


import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import eu.decentholo.holograms.event.DecentHologramsReloadEvent;

@UtilityClass
public class EventFactory {

   

    public static void handleReloadEvent() {
        if (DecentHologramsReloadEvent.getHandlerList().getRegisteredListeners().length == 0) {
            return;
        }

        DecentHologramsReloadEvent event = new DecentHologramsReloadEvent();
        Bukkit.getPluginManager().callEvent(event);
    }

}
