package fr.black_eyes.lootchest.compatibilties;

import org.bukkit.Location;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;

public class FactionsX {
	public static Boolean isInXClaim(Location loc) {
		Faction f = GridManager.INSTANCE.getFactionAt(loc.getChunk());
		
		return !f.isWilderness();
	}

}
