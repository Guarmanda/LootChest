// 
// Decompiled by Procyon v0.5.29
// 

package fr.black_eyes.lootchest.falleffect;

import org.bukkit.Location;

public class LocationUtils
{
    public static Location offset(final Location original, final double offx, final double offy, final double offz) {
        final double newX = original.getX() + offx;
        double newY = original.getY() + offy;
        final double newZ = original.getZ() + offz;
        if (newY > 255.0) {
            newY = 255.0;
        }
        else if (newY < 0.0) {
            newY = 0.0;
        }
        return new Location(original.getWorld(), newX, newY, newZ);
    }

    private LocationUtils() {
        throw new IllegalStateException("Utility class");
    }
}
