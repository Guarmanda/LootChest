package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.line.hologram.TextSequentialLoader;

import lombok.Getter;

/**
 * Creates an hologram with an armorstand, related to a lootchest (for location and text only)
 * @author Valentin
 *
 */
public class LootChestHologram {

	private static HologramPool pool;
	
	private static HologramPool getPool() {
		if(pool == null) {
			pool = new HologramPool((Plugin)Main.getInstance(), 1200);
		}
		return pool;
	}

	private Hologram hologram;

	//represents all the null names that can be given to an hologram to not create an holo
	private static List<String> NULL_NAME = new ArrayList<String>(
			Arrays.asList("\"\"" ,"\" \"" ,"null" ,"" ," " ,"_" ,"none")
			); 
	/**
	 * @return the text displayed by the hologram
	 */
	@Getter private String text;
	/**
	 * @return the location of the hologram
	 */
	@Getter private Location location;
	private Lootchest chest;
	private BukkitRunnable runnable;
	/**
	 * @param chest the chest linked with this holo
	 */
	public LootChestHologram(Lootchest chest) {
		this.chest = chest;
		this.location = chest.getActualLocation();
	}
	
	/**
	 * set the text of hologram
	 * setting the text also create the armorstand if not created
	 * @param loca The location to set the hologram
	 */
	public void setLoc(Location loca) {
		if(Main.getVersion()>7 && Main.configs.UseHologram){
			Location loc2 = loca.clone();
			loc2.add(0.5, Main.configs.Hologram_distance_to_chest-1.75, 0.5);
			this.location = loc2;
			remove();
			this.setText(chest.getHolo());
			if(!NULL_NAME.contains(text) && Main.configs.TIMER_Show_Timer && chest.getTime() != -1) {
				if(runnable == null) {
					startShowTime();
				}
				if(Main.getVersion()>8 && runnable.isCancelled()) {
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
	}

	
	/**
	 * Kills the hologram
	 */
	public void remove() {
		if(Main.getVersion()>7){
			if(runnable != null) {
				runnable.cancel();
				runnable = null;
			}
			if(hologram == null) return;
			Hologram as = getHologram();
			if(as!=null) {
				pool.remove(as);
				hologram = null;
			}
		}
	}
	
	/**
	 * @param name The text displayed by the hologram
	 */
	public void setText(String name) {
		if(!NULL_NAME.contains(name)) {
			hologram = getHologram();
			setName(Utils.color(name));
		}else {
			remove();
		}
	}

	/**
	 * Manage hologram lines to display / change its name
	 * @param name
	 */
	private void setName(String name){
		hologram.getLines().clear();
		Line line = new Line((Plugin)Main.getInstance());
		// compose an TextLine hologram
		TextLine textLine = new TextLine(line, name, null, false);
		hologram.load(textLine);
	}
	
	/**
	 * @return Creates the hologram as an invisible armorstand
	 */
	private Hologram createHologram() {
		Hologram holo =  new Hologram((Plugin)Main.getInstance(), location, new TextSequentialLoader());
		getPool().takeCareOf(holo);
		hologram = holo;
		return holo;
	}
	
	/**
	 * Doesn't throw a party.
	 * Shows a timer on the hologram if the config says it
	 */
	private void startShowTime() {
		runnable = new BukkitRunnable() {
    		public void run() {
    			Hologram as = getHologram();
    			long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime()/1000;
    			long secondes = chest.getTime()*60;
    			long tempsenregistre = chest.getLastreset()/1000;
    			secondes = secondes - (tempsactuel - tempsenregistre);
    			long secs = secondes%60;
    			long mins = (secondes%3600)/60; 
    			long hours = secondes/3600;
    			String text = Main.configs.TIMER_Format;
    			if(hours <1) text = text.replace("%Hours", "").replace("%Hsep", "");
    			if(mins <1) text = text.replace("%Minutes", "").replace("%Msep", "");
    			text = text.replace("%Hours", hours+"").replace("%Hsep", Main.configs.TIMER_H_Sep)
    					.replace("%Minutes", mins+"").replace("%Msep", Main.configs.TIMER_M_Sep)
    					.replace("%Seconds", secs+"").replace("%Ssep", Main.configs.TIMER_S_Sep)
    					.replace("%Hologram", text);
    			if(as ==null) {
    				runnable.cancel();
    			}else {
					//replace with paragraph character
    				setName(Utils.color(text));
    			}
    			if(secondes<=0) {
    				runnable.cancel();
    			}
	    	}
	    };
	    runnable.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
	
	/**
	 * @return The armorstand entity involved in the hologram
	 */
	private Hologram getHologram() {
		if(hologram==null) {
			createHologram();
		}
		return hologram;
	}
	
}
