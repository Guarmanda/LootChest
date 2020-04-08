/*package fr.black_eyes.lootchest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LootChest {
	String name;
	Location globalLoc;
	Location randomLoc;
	Inventory inv;
	Boolean fall;
	Integer[] chances;
	String direction;
	String holo;
	Integer time;
	Long lastreset;
	String particle;
	
	LootChest(String naming) {
		chances = new Integer[27];
		inv = Bukkit.createInventory(null, 27);
		fall = true;
		randomLoc = null;
		name = naming;
		holo = naming;
		ItemStack test = null;
		String str =  test.toString();
	
	}
	
	LootChest(String naming, Location globloc, Location ranloc, Inventory inv, Boolean fall, Integer[] chance, String dir, String hologram, Integer t, Long reset, String part) {

	}
	
	void setReset(long reset) {				lastreset = reset; 	}
	void setHolo(String h) {				holo = h; 			}
	void setChance(int c, int v) {			chances[c] = v;		}
	void setDirection(String dir) {			direction = dir;	}
	void setParticle(String part) {			particle = part;	}
	void setGlobalLocation(Location loc) {	globalLoc = loc;	}
	void setRandomLocation(Location loc) {	randomLoc = loc;	}
	
}*/
