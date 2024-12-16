package fr.black_eyes.lootchest;

import org.bukkit.Bukkit;
import org.bukkit.Location;



import fr.black_eyes.lootchest.compatibilties.*;

public class ProtectedRegions {
	public static Boolean isProtected(Location loc) {
    	if(Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard") && Worldguard.isInRegion(loc)) return true;

		if(Bukkit.getServer().getPluginManager().isPluginEnabled("Residence") && Residences.isInResidence(loc)) return true;

		if(Bukkit.getServer().getPluginManager().isPluginEnabled("Factions") && Factions.isInClaim(loc)) return true;

		if(Bukkit.getServer().getPluginManager().isPluginEnabled("FactionsX") && FactionsX.isInXClaim(loc)) return true;

		if(Bukkit.getServer().getPluginManager().isPluginEnabled("Towny") && Towny.isInTown(loc)) return true;

		if(Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention") && GriefPreventions.isInGriefPreventionClaim(loc)) return true;

		return false;
	}

	private ProtectedRegions() {
		throw new IllegalStateException("Utility class");
	}
}
