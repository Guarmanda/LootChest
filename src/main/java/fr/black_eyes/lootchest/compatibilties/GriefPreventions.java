package fr.black_eyes.lootchest.compatibilties;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventions  {
	public static Boolean isInGriefPreventionClaim(Location loc) {
		GriefPrevention grief = (GriefPrevention) Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");
		Claim claim = grief.dataStore.getClaimAt(loc, true, null);
		return claim != null;
	}
}
