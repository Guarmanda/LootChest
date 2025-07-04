package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import org.bukkit.scheduler.BukkitRunnable;

import eu.decentholo.holograms.api.DHAPI;
import eu.decentholo.holograms.api.holograms.Hologram;
import fr.black_eyes.simpleJavaPlugin.Utils;
import lombok.Getter;

/**
 * Creates a hologram with an armorstand, related to a lootchest (for location and text only)
 * @author Valentin
 *
 */
public class LootChestHologram {

	//represents all the null names that can be given to a hologram to not create a holo
	private static final List<String> NULL_NAME = new ArrayList<>(
			Arrays.asList("\"\"" ,"\" \"" ,"null" ,"" ," " ,"_" ,"none")
			); 

	/**
	 * Holograms don't spawn at same height on each version, so we need to modify the y position
	 * Key is the version, value is the y modifier
	 * @return the yPosModifier
	 */
	private static final Map<Integer, Double> yPosModifier = initializeRates();

	private static final double DEFAULT_RATE = 0.5;

	private static Map<Integer, Double> initializeRates() {
		Map<Integer, Double> rates = new HashMap<>();
		rates.put(8, 0.6);
		for (int i = 9; i <= 20; i++) {
			rates.put(i, DEFAULT_RATE);
		}
		return rates;
	}

	/**
	 * @return the text displayed by the hologram
	 */
	@Getter private String text;
	/**
	 * @return the location of the hologram
	 */
	@Getter private Location location;
	private final Lootchest chest;
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
	 * @param location The location to set the hologram
	 */
	public void setLoc(Location location) {
		if(Main.getCompleteVersion()>=1080 && Main.configs.usehologram){
			Location loc2 = location.clone();
			loc2.add(0.5, Main.configs.Hologram_distance_to_chest+  yPosModifier.getOrDefault(Main.getVersion(), 0.5), 0.5);
			this.location = loc2;
			remove();
			this.setText(chest.getHolo());
			if(!NULL_NAME.contains(text) && Main.configs.timerShowTimer && chest.getTime() != -1) {
				if(runnable == null) {
					startShowTime();
				}
				if(Main.getCompleteVersion()>=1094 && runnable.isCancelled()) {
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



	private Hologram hologram;

	
	/**
	 * Kills the hologram
	 */
	public void remove() {
		if(Main.getCompleteVersion()>=1080 && Main.configs.usehologram) {
			if(runnable != null) {
				runnable.cancel();
				runnable = null;
			}
			if(hologram!=null) {
				hologram.destroy();
				hologram = null;
			}

		}
	}
	
	/**
	 * @param name The text displayed by the hologram
	 */
	public void setText(String name) {
		text = name;
		if(Main.getCompleteVersion()<1080 || !Main.configs.usehologram) return;
		if(!NULL_NAME.contains(name)) {
			getHologram();
			setLine(Utils.color(name));
		}else {
			remove();
		}
	}

	/**
	 * Manage hologram lines to display / change its name
	 * @param text The text to display
	 */
	private void setLine(String text){
		if(hologram.getPage(0).getLines().isEmpty()) {
			DHAPI.addHologramLine(hologram, 0, text);
		}
		else{
			hologram.getPage(0).getLine(0).setContent(text);
			hologram.getPage(0).getLine(0).updateWithTextForAllViewers(text);
		}
	}

	private void createHologram() {
		text = chest.getHolo();
		hologram = DHAPI.createHologram(chest.getName(), location);

	}

	/**
	 * @return The hologram
	 */
	private Hologram getHologram() {
		if(hologram==null) {
			createHologram();
		}
		return hologram;
	
	}
	
	/**
	 * Doesn't throw a party.
	 * Shows a timer on the hologram if the config says it
	 */
	private void startShowTime() {
		runnable = new BukkitRunnable() {
    		public void run() {
    			Hologram holo = getHologram();
    			long tempsActuel = (new Timestamp(System.currentTimeMillis())).getTime()/1000;
    			long secondes = chest.getTime()*60;
    			long tempsEnregistre = chest.getLastReset()/1000;
    			secondes = secondes - (tempsActuel - tempsEnregistre);
    			long secs = secondes%60;
    			long mins = (secondes%3600)/60; 
    			long hours = secondes/3600;
    			String text = Main.configs.timerFormat;
				if(text != null && Main.configs.timerHSep != null && Main.configs.timerMSep != null && Main.configs.timerSSep != null) {
					if(hours <1) text = text.replace("%Hours", "").replace("%Hsep", "");
					if(mins <1) text = text.replace("%Minutes", "").replace("%Msep", "");
					text = text.replace("%Hours", hours+"").replace("%Hsep", Main.configs.timerHSep)
							.replace("%Minutes", mins+"").replace("%Msep", Main.configs.timerMSep)
							.replace("%Seconds", secs+"").replace("%Ssep", Main.configs.timerSSep)
							.replace("%Hologram", getText());

					if(holo ==null) {
						runnable.cancel();
					}else {
						//replace with paragraph character
						setLine(Utils.color(text));
					}
				}
    			if(secondes<=0) {
    				runnable.cancel();
    			}
	    	}
	    };
	    runnable.runTaskTimer(Main.getInstance(), 0, 20);
	}
	
}
