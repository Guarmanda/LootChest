package fr.black_eyes.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.black_eyes.lootchest.Lootchest;
import lombok.Getter;

public class LootChestSpawnEvent extends Event {

    @Getter private Lootchest lc;

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * the lootchest concerned by the event
     * @param lc
     */
    public LootChestSpawnEvent(Lootchest lc) {
        this.lc = lc;
    }
    
}
