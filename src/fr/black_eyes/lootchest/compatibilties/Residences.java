package fr.black_eyes.lootchest.compatibilties;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class Residences  {
	public static Boolean isInResidence(Location loc) {
		Residence residence = (Residence) Bukkit.getServer().getPluginManager().getPlugin("Residence");
		
		@SuppressWarnings("static-access")
		ClaimedResidence res = residence.getAPI().getResidenceManager().getByLoc(loc);


		return res != null;
	}
}
