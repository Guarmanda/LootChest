package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.holoeasy.HoloEasy;
import org.holoeasy.config.HologramKey;
import org.holoeasy.hologram.Hologram;
import org.holoeasy.hologram.TextSequentialLoader;
import org.holoeasy.line.TextLine;
import org.holoeasy.pool.IHologramPool;

import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.line.Line;

import lombok.Getter;

/**
 * Creates an hologram with an armorstand, related to a lootchest (for location and text only)
 * @author Valentin
 *
 */
public class LootChestHologram {

	//represents all the null names that can be given to an hologram to not create an holo
	private static List<String> NULL_NAME = new ArrayList<String>(
			Arrays.asList("\"\"" ,"\" \"" ,"null" ,"" ," " ,"_" ,"none")
			); 

	/**
	 * Holograms dont spawn at same height on each version, so we need to modify the y position
	 * Key is the version, value is the y modifier
	 * @return the yPosModifier
	 */
	private static Map<Integer, Double> yPosModifier = new HashMap<Integer, Double>(){{
		put(8, -1.0);
		put(9, -1.8);
		put(10, -1.8);
		put(11, -1.8);
		put(12, -1.8);
		put(13, -1.8);
		put(14, -1.8);
		put(15, -1.8);
		put(16, -1.8);
		put(17, -1.8);
		put(18, -1.8);
		put(19, -1.8);
		put(20, -1.8);
	}};
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
			loc2.add(0.5, Main.configs.Hologram_distance_to_chest+  yPosModifier.getOrDefault(Main.getVersion(), -1.0), 0.5);
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

	private static IHologramPool pool;

	//for 1.8
	private static HologramPool pool1_8;

	private static HologramPool getPool1_8() {
		if(pool1_8 == null) {
			pool1_8 = new HologramPool((Plugin)Main.getInstance(), 1200);
		}
		return pool1_8;
	}
	
	private static IHologramPool getPool() {
		if(pool == null) {
			pool = HoloEasy.startPool((Plugin)Main.getInstance(), 1200);
		}
		return pool;
	}

	private Hologram hologram;
	private com.github.unldenis.hologram.Hologram hologram1_8;

	
	/**
	 * Kills the hologram
	 */
	public void remove() {
		if(Main.getVersion()>7){
			if(runnable != null) {
				runnable.cancel();
				runnable = null;
			}
			if(hologram == null && hologram1_8 == null) return;
			if(Main.getVersion()>8 && hologram!=null) {
				pool.remove(hologram.getKey());
				hologram = null;
			}
			else if(hologram1_8!=null && Main.getVersion()==8) {
				getPool1_8().remove(hologram1_8);
				hologram1_8 = null;
			}
		}
	}
	
	/**
	 * @param name The text displayed by the hologram
	 */
	public void setText(String name) {
		if(!NULL_NAME.contains(name)) {
			getHologram();
			setLine(Utils.color(name));
		}else {
			remove();
		}
	}

	/**
	 * Manage hologram lines to display / change its name
	 * @param name
	 */
	private void setLine(String name){
		// compose a TextLine hologram
		if(Main.getVersion()>8) {
			TextLine textLine = new TextLine((Plugin)Main.getInstance(), name, null, false);
			hologram.load(textLine);
		}
		else {
			Line line = new Line((Plugin)Main.getInstance());
			com.github.unldenis.hologram.line.TextLine textLine = new com.github.unldenis.hologram.line.TextLine(line, name, null, false);
			hologram1_8.load(textLine);
		}
	}
	
	/**
	 * @return Creates the hologram 
	 */
	private Object createHologram() {
		if(Main.getVersion()>8){
			HologramKey key = new HologramKey(getPool(), chest.getName());
			hologram =  new Hologram(key, location, new TextSequentialLoader());
			return hologram;
		}
		else {
			hologram1_8 =  new com.github.unldenis.hologram.Hologram((Plugin)Main.getInstance(), location, new com.github.unldenis.hologram.line.hologram.TextSequentialLoader());
			getPool1_8().takeCareOf(hologram1_8);
			return hologram1_8;
		}
		
	}

	/**
	 * @return The hologram
	 */
	private Object getHologram() {
		if(hologram==null && hologram1_8==null) {
			createHologram();
		}
		if(Main.getVersion()>8)
			return hologram;
		else
			return hologram1_8;
	}
	
	/**
	 * Doesn't throw a party.
	 * Shows a timer on the hologram if the config says it
	 */
	private void startShowTime() {
		runnable = new BukkitRunnable() {
    		public void run() {
    			Object as = getHologram();
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
    				setLine(Utils.color(text));
    			}
    			if(secondes<=0) {
    				runnable.cancel();
    			}
	    	}
	    };
	    runnable.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
}
