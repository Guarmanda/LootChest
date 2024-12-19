package fr.black_eyes.lootchest.compatibilties;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class Residences  {
	public static boolean isInResidence(Location loc) {
		Residence residence = (Residence) Bukkit.getServer().getPluginManager().getPlugin("Residence");

        ClaimedResidence res = null;
        if (residence != null) {
            res = ResidenceApi.getResidenceManager().getByLoc(loc);
        }


        return res != null;
	}

	private Residences() {
		throw new IllegalStateException("Utility class");
	}
}
