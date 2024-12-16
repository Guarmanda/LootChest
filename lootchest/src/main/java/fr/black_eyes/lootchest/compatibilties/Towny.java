package fr.black_eyes.lootchest.compatibilties;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.TownyAPI;

public class Towny {
	public static boolean isInTown(Location loc) {
		return !TownyAPI.getInstance().isWilderness(loc);
	}

	private Towny() {
		throw new IllegalStateException("Utility class");
	}
}
