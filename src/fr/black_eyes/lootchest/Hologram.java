package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;

/**
 * Creates an hologram with an armorstand, related to a lootchest (for location and text only)
 * @author Valentin
 *
 */
public class Hologram {
	//represents all the null names that can be given to an hologram to not create an holo
	private static List<String> NULL_NAME = new ArrayList<String>(
			Arrays.asList("\"\"" ,"\" \"" ,"null" ,"" ," " ,"_" ,"none")
			); 
	@Getter private String text;
	@Getter private Location location;
	private Lootchest chest;
	private BukkitRunnable runnable;
	private UUID uuid;
	public Hologram(Lootchest chest) {
		this.chest = chest;
		this.location = chest.getActualLocation();
	}
	
	public void setLoc(Location loca) {
		//Entity as = getArmorstand();
		Location loc2 = new Location(loca.getWorld(), loca.getX(), loca.getY(), loca.getZ());
		loc2.add(0.5, Main.configs.Hologram_distance_to_chest, 0.5);
		this.location = loc2;
		remove();
		this.setText(chest.getHolo());
		if(!NULL_NAME.contains(text) && Main.configs.TIMER_Show_Timer && chest.getTime() != -1) {
			if(runnable == null) {
				startShowTime();
			}
			if(runnable.isCancelled()) {
				try {
					runnable.runTaskTimer(Main.getInstance(), 0, 20);
				}catch(IllegalStateException e) {
					runnable.cancel();
					runnable = null; 
					startShowTime();
				}
			}
		}
	}

	
	public void remove() {
		if(runnable != null) {
			runnable.cancel();
			runnable = null;
		}
		Entity as = getArmorstand();
		if(as!=null) {
			as.remove();
			uuid=null;
		}

	}
	
	public void setText(String name) {
		text = name;
		org.bukkit.entity.ArmorStand as = getArmorstand();
		if((as == null || as.isDead()) && !NULL_NAME.contains(text)) {
			as = createArmorStand();
		}
		if(!NULL_NAME.contains(text)) {
			name = text.replace("&", "§");
			as.setCustomName(name);
		}else {
			remove();
		}
	}
	
	private org.bukkit.entity.ArmorStand createArmorStand() {
		org.bukkit.entity.ArmorStand as = (org.bukkit.entity.ArmorStand) location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.ARMOR_STAND); //Spawn the ArmorStand
		uuid = as.getUniqueId();
		as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not

		as.setGravity(false); //Make sure it doesn't fall
		as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
		//This makes the text appear no matter if your looking at the entity or not
		as.setVisible(false); //Makes the ArmorStand invisible
	 	as.setArms(false);
	 	as.setBasePlate(false);
	 	as.setSmall(true);
	 	if(!org.bukkit.Bukkit.getVersion().contains("1.8)")) {
	 		as.setMarker(true);
		}
	 	return as;
	}
	
	private void startShowTime() {
		runnable = new BukkitRunnable() {
    		public void run() {
    			Entity as = getArmorstand();
    			long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime()/1000;
    			long secondes = chest.getTime()*60;
    			long tempsenregistre = chest.getLastreset()/1000;
    			secondes = secondes - (tempsactuel - tempsenregistre);
    			long secs = secondes%60;
    			long mins = (secondes%3600)/60; 
    			long hours = secondes/3600;
    			String hologram = Main.configs.TIMER_Format;
    			if(hours <1) hologram = hologram.replace("%Hours", "").replace("%Hsep", "");
    			if(mins <1) hologram = hologram.replace("%Minutes", "").replace("%Msep", "");
    			hologram = hologram.replace("%Hours", hours+"").replace("%Hsep", Main.configs.TIMER_H_Sep)
    					.replace("%Minutes", mins+"").replace("%Msep", Main.configs.TIMER_M_Sep)
    					.replace("%Seconds", secs+"").replace("%Ssep", Main.configs.TIMER_S_Sep)
    					.replace("%Hologram", text);
    			as.setCustomName(hologram.replace("&", "§"));
    			if(secondes<=0) {
    				runnable.cancel();
    			}
	    	}
	    };
	    runnable.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
	
	private org.bukkit.entity.ArmorStand getArmorstand() {
		if(uuid==null) {
			for(Entity ent : location.getWorld().getEntities()) {
				if(ent instanceof org.bukkit.entity.ArmorStand && ent.getLocation().distance(location) <0.1) {
					Main.getInstance().logInfo("Armorstand found");
					return (ArmorStand)ent;
				}
			}
			return null;
		}
		location.getWorld().loadChunk(location.getChunk());
		if(Bukkit.getEntity(uuid) !=null) {
			Main.getInstance().logInfo("Armorstand found (uuid)");
			return (ArmorStand)Bukkit.getEntity(uuid);
		}
		return null;
	}
}
