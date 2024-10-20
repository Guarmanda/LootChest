package fr.black_eyes.lootchest.falleffect;

import org.bukkit.Location;

public interface IFallPacket {

    /**
     * Get the actual location of the armorstand
     * Getting it with the entity class won't work because I move the armorstand, but only client sees it moving,
     * so I need to get the location from the start location and the counter
     * @return Location of the armorstand
     */
    Location getLocation();

    /**
     * Sends the four packets to all players that are in a 100 blocks radius of the armorstand
     * An entity can't have gravity with packets, so we will manually move it to the ground, ticks after ticks
     */
    void sendPacketToAll();

    /**
     * Removes the armorstand from all players
     */
    void removePacketToAll();
    
}
