package fr.black_eyes.lootchest;

import org.bukkit.Bukkit;
import org.bukkit.Location;



import fr.black_eyes.lootchest.compatibilties.*;

public class ProtectedRegions {
	public static Boolean isProtected(Location loc) {
    	if(Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard"))	  {
    		if(Worldguard.isInRegion(loc)) return true;
    	}
    	if(Bukkit.getServer().getPluginManager().isPluginEnabled("Residence")) {
    		if(Residences.isInResidence(loc)) return true;
    	}
    	if(Bukkit.getServer().getPluginManager().isPluginEnabled("Factions")) {
    		if(Factions.isInClaim(loc)) return true;
    	}
    	if(Bukkit.getServer().getPluginManager().isPluginEnabled("Towny")) {
    		if(Towny.isInTown(loc)) return true;
    	}
    	return false;
	}
}
