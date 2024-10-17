package fr.black_eyes.lootchest.compatibilties;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.TownyAPI;

public class Towny {
	public static Boolean isInTown(Location loc) {
		return !TownyAPI.getInstance().isWilderness(loc);
	}
}
