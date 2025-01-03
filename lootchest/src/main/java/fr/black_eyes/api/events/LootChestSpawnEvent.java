package fr.black_eyes.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.black_eyes.lootchest.Lootchest;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class LootChestSpawnEvent extends Event {

    private final Lootchest lc;

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    //seems like it is needed, else listening to the event throws an error? let's add it
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * the lootchest concerned by the event
     * @param lc the lootchest
     */
    public LootChestSpawnEvent(Lootchest lc) {
        this.lc = lc;
    }
    
}
