package fr.black_eyes.lootchest;

import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import lombok.Setter;
public class Lootchest extends Utils {
	@Getter @Setter String name;
	@Setter Location globalLoc;
	@Setter Location randomLoc;
	Inventory inv;
	@Getter @Setter Boolean fall;
	Integer[] chances;
	@Setter String direction;
	@Getter @Setter String holo;
	@Getter @Setter long time;
	@Getter @Setter long lastreset;
	@Getter @Setter String particle;
	@Getter @Setter Boolean respawn_cmd;
	@Getter @Setter Boolean respawn_natural;
	@Getter @Setter Boolean take_msg;
	@Getter @Setter int radius;
	@Getter @Setter String world;
	
	
	/*Function used in Main / reload for chest loading */
	public Lootchest(String naming) {
		name = naming;
		Integer[] chancesInit = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		chances = chancesInit;
		chancesInit = null;
		inv = Bukkit.createInventory(null, 27);
		globalLoc = getPosition(naming);
		if(configFiles.getData().isSet("chests."+naming+".randomradius")) {
			radius = configFiles.getData().getInt("chests."+naming+".randomradius");
			if(radius > 0) {
				randomLoc = getRandomPosition(naming);
			}else {
				randomLoc = null;
			}
		}
		
		else {
			radius = 0;
			randomLoc = null;
		}
		holo = configFiles.getData().getString("chests." + naming + ".holo");
		particle = configFiles.getData().getString("chests." + naming + ".particle");
		time = configFiles.getData().getInt("chests." + naming + ".time");
		fall =  configFiles.getData().getBoolean("chests." + naming + ".fall");
		try {
		for(String keys : configFiles.getData().getConfigurationSection("chests." + naming + ".inventory").getKeys(false)) {
			inv.setItem(Integer.parseInt(keys), configFiles.getData().getItemStack("chests." + naming + ".inventory." + keys));
			chances[Integer.parseInt(keys)] = configFiles.getData().getInt("chests." + naming + ".chance." + keys);
		}
		}catch(NullPointerException e) {
			Main.logInfo("&cMaybe you changed to an older server version recently: chest inventory of "+name+" was lost :/");
		}
		respawn_cmd =  configFiles.getData().getBoolean("chests." + naming + ".respawn_cmd");
		respawn_natural =  configFiles.getData().getBoolean("chests." + naming + ".respawn_natural");
		take_msg =  configFiles.getData().getBoolean("chests." + naming + ".take_message");
		world = configFiles.getData().getString("chests." + naming + ".position.world");
		direction = configFiles.getData().getString("chests." + naming + ".direction");
		lastreset = configFiles.getData().getLong("chests." + name + ".lastreset");
	}
	
	
	/*Function used for /lc create */
	public Lootchest(Block chest, String naming){

		name = naming;
		inv = Bukkit.createInventory(null, 27);
		Integer[] chancesInit = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		chances = chancesInit;
		Inventory inve = ((Chest) chest.getState()).getInventory();
		for(int i = 0 ; i < inve.getSize() ; i++) {
			if(inve.getItem(i) != null) {
				inv.setItem( i, inve.getItem(i));
				chances[i] =  Main.configs.default_item_chance;
			}
		}
		fall =  Main.configs.FALL_Enabled;
		respawn_cmd =  Main.configs.NOTE_command_e;
		respawn_natural =  Main.configs.NOTE_natural_e;
		take_msg =  Main.configs.NOTE_message_on_chest_take;
		direction = getDirection(chest);
		holo = name;
		time =  Main.configs.default_reset_time;
		globalLoc =  chest.getLocation();
		lastreset =  new Timestamp(System.currentTimeMillis()).getTime();
	   	particle =  Main.configs.PART_default_particle;
	   	radius = 0;
	   	world = chest.getWorld().getName();
		((Chest) chest.getLocation().getBlock().getState()).getInventory().clear();
		chest.getLocation().getBlock().setType(Material.AIR);
		
	}
	
	/*Function used at defined time in config and at plugin stop for saving chests */
	void saveInConfig(){

		configFiles.getData().set("chests." + name + ".inventory", null);
			for(int i = 0 ; i < inv.getSize() ; i++) {
				if(inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
					configFiles.getData().set("chests." + name + ".inventory." + i, inv.getItem(i));
					configFiles.getData().set("chests." + name + ".chance." + i, chances[i]);
				}
			}
			configFiles.getData().set("chests." + name + ".fall", fall);
			configFiles.getData().set("chests." + name + ".respawn_cmd", respawn_cmd);
			configFiles.getData().set("chests." + name + ".respawn_natural", respawn_natural);
			configFiles.getData().set("chests." + name + ".take_message", take_msg);
			
			configFiles.getData().set("chests." + name + ".direction", direction);
			configFiles.getData().set("chests." + name + ".holo", holo);
			configFiles.getData().set("chests." + name + ".time", time);
			setPosition(name, globalLoc);
			configFiles.getData().set("chests." + name + ".lastreset", lastreset);
			configFiles.getData().set("chests." +name+ ".particle", particle);
		   	configFiles.getData().set("chests."+name+".randomradius", radius);
		   	if(randomLoc != null) {
		   		setRandomPosition(name, randomLoc);
		   	}

	}
	

	public Location getPosition() {			return globalLoc.clone();	}
	public Location getRandomPosition() {	return (randomLoc!=null)?randomLoc.clone():null;	}


	public Location getActualLocation() {
		return (radius!=0)?randomLoc.clone():globalLoc.clone();
	}
	
	public void setChance(int c, int v) {			chances[c] = v;		}


	public void setInventory(Inventory inve) {
		for(int i = 0 ; i < inve.getSize() ; i++) {
			if(inve.getItem(i) != null) {
				inv.setItem( i, inve.getItem(i));
				if(chances[i] ==0) {
					chances[i] =  Main.configs.default_item_chance;
				}
			}
		}
	}



	
	
}