package fr.black_eyes.lootchest.compatibilties;

import org.bukkit.Location;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.ps.PS;

public class Factions {
	public static Boolean isInClaim(Location loc) {
		Faction f = BoardColl.get().getFactionAt(PS.valueOf(loc));
	    return (!f.equals(FactionColl.get().getNone()));
	}
}
