package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.commands.LootchestCommand;
import fr.black_eyes.lootchest.falleffect.FallingPackageEntity;

public class Utils  {
	Main instance = Main.getInstance();
	Config config = Main.getConfigFiles();
	long timing;
	
	//message functions that automatically get a message from config.getLang()uage file
	public void msg(CommandSender p, String path, String replacer, String replacement) {
		p.sendMessage(getMsg(path, replacer, replacement));
	}
	
	public String getMsg(String path, String replacer, String replacement) {
		return config.getLang().getString(path).replace(replacer, replacement).replace("&", "§");
	}
	
	
	//function to copy a chest
	//fonction pour copier un coffre
	public void copychest(Lootchest chest1, Lootchest chest2) {
		chest2.holo = chest1.holo;
		chest2.chances = chest1.chances.clone();
		chest2.direction = chest1.direction;
		chest2.fall = chest1.fall;
		chest2.inv.setContents(chest2.inv.getContents());
		chest2.time = chest1.time;
		chest2.particle = chest1.particle;
		chest2.respawn_cmd = chest1.respawn_cmd;
		chest2.respawn_natural = chest1.respawn_natural;
		chest2.globalLoc = chest1.globalLoc.clone();
		chest2.randomLoc = chest1.randomLoc.clone();
		chest2.take_msg = chest1.take_msg;
		chest2.radius = chest1.radius;
		
		Main.getConfigFiles().reloadData();
		restoreChest(chest2, true);
	}
	

	
	//cr§er le coffe et enregistrer les infos
	//chest creation and registering

	
	
	//fonction pour changer la position d'un coffre
	//function to change a chest location
	public void changepos(Lootchest name, Location loc3) {
		Location loc = name.getActualLocation();
		if(Bukkit.getWorld(name.world) != null && loc.getBlock().getType() == Material.CHEST) {
			Block chest = loc.getBlock();
			deleteholo(loc);
			((Chest) chest.getLocation().getBlock().getState()).getInventory().clear();
			chest.setType(Material.AIR);
			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			loc2.setX(loc.getX()+0.5);
			loc2.setY(loc.getY()+0.5);
			loc2.setZ(loc.getZ()+0.5);
			Main.part.remove(loc2);
		}
		name.world = loc3.getWorld().getName();
		name.globalLoc = loc3;
		restoreChest(name, true);
	}
	
	//deletes a chest
	//supprimes un coffre
	public void deleteChest(Lootchest lc) {
		Location loc = lc.globalLoc;
		if(lc.randomLoc != null) {
			loc = lc.randomLoc;
		}
		Block chest = loc.getBlock();
		if(chest.getType().equals(Material.CHEST)) {
			((Chest) chest.getState()).getInventory().clear();
		}
		chest.setType(Material.AIR);
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.setX(loc.getX()+0.5);
		loc2.setY(loc.getY()+0.5);
		loc2.setZ(loc.getZ()+0.5);
		Main.part.remove(loc2);
		deleteholo(chest.getLocation());
		Main.LootChest.remove(lc.name);
		
	}
	
	public void sheduleRespawn(Lootchest lc) {

		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		long minutes = lc.time;
		long tempsenregistre = lc.lastreset;
		
		if( minutes<1) {
			return;
		}
		//Bukkit.getLogger().info("spawning of "+ key + " in "+ (minutes*60-((tempsactuel - tempsenregistre)/1000)) +"seconds");
		//Main.getInstance().getLogger().info("Sheduler launched for chest " + key + " : " + (minutes*60-((tempsactuel - tempsenregistre)/1000)) + " seconds");
		new BukkitRunnable() {       
            @Override
            public void run() {
            	restoreChest(lc, false);
            }                
        }.runTaskLater(Main.getInstance(), (minutes*60-((tempsactuel - tempsenregistre)/1000))*20);
    }
	
	//to fill a chest or give chest to player
	public void fillInventory(Lootchest name, Inventory inv, boolean clear, Player p) {
		if(clear) {
			inv.clear();
		}
		if(p!=null) {
			for(ItemStack keys : name.inv.getContents()) {
				if(keys == null || keys.getType().equals(Material.AIR)) {
					
				}
				else if(p.getInventory().firstEmpty() != -1) {
					inv.addItem(keys);
				}else {
					p.getWorld().dropItem(p.getLocation(), keys);
				}
			}
		}
		else {
			for(int i=0; i<=26; i++) {
				inv.setItem(i, name.inv.getItem(i));
			}
		}
		
	}
	
	
	void reactivateEffects(Lootchest lc) {
		Location loc = lc.getActualLocation();
		if(!loc.getBlock().getType().equals(Material.CHEST)) {
			return;
		}
		if(Main.getInstance().getConfig().getBoolean("UseHologram")){
			deleteholo(loc);
			makeHolo(loc, lc);
		}
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.add(0.5,0.5,0.5);
		for(Object part : Main.particules) {
			if((""+part).contains(lc.particle)) {
				Main.part.put(loc2, part);
			}
		}
		
	}
	
	//gives a random number from -max to max
	public int randomInt(int max) {
		return ThreadLocalRandom.current().nextInt(0-max, max+1);
	}
	
	//se sert du config.getData().yml pour set le coffre et remplir son inventaire, cr§er l'holo en fonction du nom 
	//Taking informations from config.getData().yml to restore a specific chest if it is time to do it, or if we force respawn. 
	public boolean restoreChest(Lootchest lc, Boolean force) {

		Integer num = config.getConfig().getInt("Minimum_Number_Of_Players_For_Natural_Spawning");
		int players = 0;
		if(org.bukkit.Bukkit.getVersion().contains("1.7")) {
			players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
		}else {
			players = org.bukkit.Bukkit.getOnlinePlayers().size();
		}
		//Main.getInstance().getLogger().info("respawn function of "+ name + " (1)");

		if(Bukkit.getWorld(lc.world) == null) {
			Bukkit.getLogger().info("§cThe world " + lc.world + " is not loaded, can't respawn chest " + lc);
			return false;
		}
		
		//Main.getInstance().getLogger().info("respawn function of "+ name + " (2)");

		Location loc = lc.globalLoc.clone();
		Location newloc = loc;
		if(lc.radius != 0) {
			int random = lc.radius;
			Location randompos = lc.globalLoc.clone();

			randompos.setX(randomInt(random)+loc.getX());
			randompos.setZ(randomInt(random)+loc.getZ());
			randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos));
			if (Bukkit.getVersion().contains("1.15")) {
				randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos)+1);
			}

			if(lc.radius !=0) {
				loc = lc.randomLoc.clone();
			}

			newloc = randompos;


			
			
		}
		final Block block = loc.getBlock();
		
		//Bukkit.getLogger().info("respawn function of "+ name + " (3)");
		final Location loc3 = loc.clone();

		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		long minutes = lc.time*60*1000;
		long tempsenregistre = lc.lastreset;

		if((tempsactuel - tempsenregistre > minutes && minutes>-1) || force) {
			int height = Main.getInstance().getConfig().getInt("Fall_Effect_Height");
			if(lc.radius!=0 && loc3 != newloc && block.getType().equals( Material.CHEST)) {
				deleteholo(loc3);
				((Chest) block.getState()).getInventory().clear();
				block.setType(Material.AIR);
				loc3.add(0.5,0.5,0.5);
				Main.part.remove(loc3);
				
			}
			if(lc.radius != 0) {
				lc.randomLoc = newloc;
			}
           // Bukkit.getLogger().info("respawn function of "+ name + " (4)");
			
			if(!force && lc.respawn_natural && num <= players ) {
				String holo = lc.holo;
				if(!Main.getInstance().getConfig().getBoolean("respawn_notify.per_world_message")) {
					Bukkit.broadcastMessage((((Main.getInstance().getConfig().getString("respawn_notify.natural_respawn.message").replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
			
				}else {
					for(Player p : loc.getWorld().getPlayers()){
						p.sendMessage((((Main.getInstance().getConfig().getString("respawn_notify.natural_respawn.message").replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
					}
				}
			}

			final Block newblock = newloc.getBlock();
			final Location theloc = newloc;
			//Bukkit.getLogger().info("respawn function of "+ name);
			if(lc.fall&& (num <= players || force) ) {
				
				Location startloc = new Location(newloc.getWorld(), newloc.getX()+0.5, newloc.getY()+height, newloc.getZ()+0.5);
				if(startloc.getWorld().isChunkLoaded(startloc.getBlockX()/16, startloc.getBlockZ()/16)) {

					new FallingPackageEntity(startloc);

		                	
		                	spawnChest(lc, newblock, theloc, force);

				}else {
					spawnChest(lc, newblock, theloc, force);
				}

				
			}
			else {
				spawnChest(lc, newblock, theloc, force);
			}
			
			if(!Main.getInstance().getConfig().getBoolean("UseHologram")){
				deleteholo(loc);

			}

			

		}
		else {
			//Main.getInstance().getLogger().info("time: "+ ((new Timestamp(System.currentTimeMillis())).getTime() - tempsenregistre) + " ; defined time: "+ minutes);
			//sheduleRespawn(name);
		}
		
		return ((tempsactuel - tempsenregistre > minutes && minutes>=0) || force);
	}
	
	
	@SuppressWarnings("deprecation")
	public  void spawnChest(Lootchest name, Block block, Location theloc, Boolean force) {
		Integer num = config.getConfig().getInt("Minimum_Number_Of_Players_For_Natural_Spawning");
		int players = 0;
		if(org.bukkit.Bukkit.getVersion().contains("1.7")) {
			players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
		}else {
			players = org.bukkit.Bukkit.getOnlinePlayers().size();
		}
		block.setType(Material.CHEST);
		if(num <= players || force) {
			Inventory inv = ((Chest) block.getState()).getInventory();
			fillInventory(name, inv, true, null);

			String direction = name.direction;
			BlockState state = block.getState();
			if(direction.equalsIgnoreCase("east")){
				state.setData(new org.bukkit.material.Chest(BlockFace.EAST));
			}
			if(direction.equalsIgnoreCase("north")){
				state.setData(new org.bukkit.material.Chest(BlockFace.NORTH));
			}			
			if(direction.equalsIgnoreCase("south")){
				state.setData(new org.bukkit.material.Chest(BlockFace.SOUTH));
			}			
			if(direction.equalsIgnoreCase("west")){
				state.setData(new org.bukkit.material.Chest(BlockFace.WEST));
			}
			state.update();
			
			final Location loc2 = name.getActualLocation();
			loc2.add(0.5,0.5,0.5);
			if(block.getType().equals(Material.CHEST) ) {
				for(Object part : Main.particules) {
					if((""+part).contains(name.particle)) {
						Main.part.put(loc2, part);
					}
				}
			}else if  (!block.getType().equals(Material.CHEST)){
				deleteholo(theloc);
				Main.part.remove(loc2);
			}
			if(Main.getInstance().getConfig().getBoolean("UseHologram")){
				deleteholo(theloc);
				makeHolo(theloc, name);
			}
		}
		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		name.lastreset = tempsactuel;
		//config.reloadData();
		//Main.getInstance().getLogger().info("Shedule respawn of chest "+ name);

		sheduleRespawn(name);
	}
	
	//check for empty inventory
	public  boolean isEmpty(Inventory inv) {
	     for (ItemStack item : inv.getContents() ) {
	       if ( item != null ) {
	         return false;
	       }
	     }
	     return true;
	}

	//check if a chest is a lootchest by looking all lootchests locations
	public  Lootchest isLootChest(Location loc) {
		for(Lootchest keys : Main.LootChest.values()) {
			Location loc2 = keys.getActualLocation();
			if(loc2.equals(loc)) {
				return keys;
			}
		}
		return null;
	}
	
	//geting chest position from config.getData().yml
	public  Location getPosition(String name) {
		if (config.getData().getString("chests." + name + ".position.world") == null) {
			instance.getLogger().info("§cThe plugin couldn't get the world of chest §6" + name +"§c. This won't prevent the plugin to work, but the plugin may throw other errors because of that.");
			return null;
		}
		World world = Bukkit.getWorld(config.getData().getString("chests." + name + ".position.world"));
		double x = config.getData().getDouble("chests." + name + ".position.x");
		double y = config.getData().getDouble("chests." + name + ".position.y");
		double z = config.getData().getDouble("chests." + name + ".position.z");
		float pitch = (float) config.getData().getDouble("chests." + name + ".position.pitch");
		float yaw = (float) config.getData().getDouble("chests." + name + ".position.yaw");
		return new Location(world, x, y, z, pitch, yaw);
	}
	
	//seting chest position in config.getData().yml
	public  void setPosition(String name, Location loc) {
		config.getData().set("chests." + name + ".position.world", loc.getWorld().getName());
		config.getData().set("chests." + name + ".position.x", loc.getX());
		config.getData().set("chests." + name + ".position.y", loc.getY());
		config.getData().set("chests." + name + ".position.z", loc.getZ());
		config.getData().set("chests." + name + ".position.pitch", loc.getPitch());
		config.getData().set("chests." + name + ".position.yaw", loc.getYaw());
	}
	
	public  void setRandomPosition(String name, Location loc) {
		config.getData().set("chests." + name + ".randomPosition.world", loc.getWorld().getName());
		config.getData().set("chests." + name + ".randomPosition.x", loc.getX());
		config.getData().set("chests." + name + ".randomPosition.y", loc.getY());
		config.getData().set("chests." + name + ".randomPosition.z", loc.getZ());
		config.getData().set("chests." + name + ".randomPosition.pitch", loc.getPitch());
		config.getData().set("chests." + name + ".randomPosition.yaw", loc.getYaw());
	}
	
	public void updateData() {
		for(Lootchest lc : Main.LootChest.values()) {
			lc.saveInConfig();
		}
		config.saveData();
	}
	
	public void updateData(Lootchest lc) {
		lc.saveInConfig();
		config.saveData();
	}
	
	public  Location getRandomPosition(String name) {
		if(!config.getData().isSet("chests." + name + ".randomPosition.x")) {
			return null;
		}
		World world = Bukkit.getWorld(config.getData().getString("chests." + name + ".randomPosition.world"));
		double x = config.getData().getDouble("chests." + name + ".randomPosition.x");
		double y = config.getData().getDouble("chests." + name + ".randomPosition.y");
		double z = config.getData().getDouble("chests." + name + ".randomPosition.z");
		float pitch = (float) config.getData().getDouble("chests." + name + ".randomPosition.pitch");
		float yaw = (float) config.getData().getDouble("chests." + name + ".randomPosition.yaw");
		return new Location(world, x, y, z, pitch, yaw);
	}
	
	//Pour suprimer un holograme
	//To delete hologram
	public  void deleteholo(Location loc) {
		if(!org.bukkit.Bukkit.getVersion().contains("1.7")){
			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			loc2.add(0.5, Main.getInstance().getConfig().getInt("Hologram_distance_to_chest"), 0.5);
			//the coordinates of a block are at the corner of the block
			for(Entity ent : loc2.getChunk().getEntities()) {
				if(ent instanceof org.bukkit.entity.ArmorStand && ent.getLocation().distance(loc2) <1.1) {
					ent.remove();
				}
			}
		}
	}
	
	//Pour faire des hologrames au dessus des coffres
	//To do holograms near chests
	//the below function was created using some spigot forums code with some modifications
	public  void makeHolo(Location loc, Lootchest lc) {
		if(!org.bukkit.Bukkit.getVersion().contains("1.7")){
			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			loc2.add(0.5, Main.getInstance().getConfig().getInt("Hologram_distance_to_chest"), 0.5);
			//the coordinates of a block are at the corner of the block
			org.bukkit.entity.ArmorStand as = (org.bukkit.entity.ArmorStand) loc2.getWorld().spawnEntity(loc2, org.bukkit.entity.EntityType.ARMOR_STAND); //Spawn the ArmorStand
	
			String name = lc.holo.replace("&", "§");
			as.setCustomName(name); //Set this to the text you want
			if(!(name.equals("\"\"") || name.equals("\" \"") || name.equals("null") || name.equals("") || name.equals(" ") || name.equals("_") || name.equals("none"))) {
				as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
			}
			else {
				as.setCustomNameVisible(false); 
			}
			as.setGravity(false); //Make sure it doesn't fall
			as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
			//This makes the text appear no matter if your looking at the entity or not
			as.setVisible(false); //Makes the ArmorStand invisible
		 	as.setArms(false);
		 	as.setBasePlate(false);
		 	as.setSmall(true);
		 	as.setMarker(true);

		}

	}	
	

	
	
	
	
	//Inventaires
	public  void invChances(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.chances.name", "[Chest]", name.name));
		for(int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if(item.getType() != Material.AIR) {
				List<String> lore = new ArrayList<String>();
				lore.add(getMsg("Menu.chances.lore", "[Chest]", name.name));
				lore.add(name.chances[i]+ "%");
				ItemMeta im = item.getItemMeta();
				im.setLore(lore);
				ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
				item2.setItemMeta(im);
				
				inv.setItem(i, item2);
			}
		}
		LootchestCommand.editinv.put(p, name.name);
		p.openInventory(inv);
	}
	
	public  void invTime(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.time.name", "[Chest]", name.name));
		inv.setItem(4, getItem(Mat.TOTEM_OF_UNDYING, getMsg("Menu.time.infinite", " ", " ")));
		
		long temps = name.getTime();
		long jours = temps/3600;
		long heures = temps/60 - jours*24;
		long minutes = temps - heures*60 - jours*3600;
		
		
		//Initialisation du menu selon le temps du coffre
		if (jours/10 == 0) {
			inv.setItem(9, getItem(Mat.BARRIER, getMsg("Menu.time.days", " ", " ")));
		} else {
			inv.setItem(9, getItem(Mat.GOLD_BLOCK, getMsg("Menu.time.days", " ", " ")));
			inv.getItem(9).setAmount((int) (jours/10));
		} if (jours/10*10 == jours) {
			inv.setItem(10, getItem(Mat.BARRIER, getMsg("Menu.time.days", " ", " ")));
		}else {
			inv.setItem(10, getItem(Mat.GOLD_BLOCK, getMsg("Menu.time.days", " ", " ")));
			inv.getItem(10).setAmount((int) (jours-jours/10*10));
		}if (heures/10 == 0) {
			inv.setItem(12, getItem(Mat.BARRIER, getMsg("Menu.time.hours", " ", " ")));
		}else {
			inv.setItem(12, getItem(Mat.GOLD_INGOT, getMsg("Menu.time.hours", " ", " ")));
			inv.getItem(12).setAmount((int) (heures/10));
		}if (heures/10*10 == heures) {
			inv.setItem(13, getItem(Mat.BARRIER, getMsg("Menu.time.hours", " ", " ")));
		}else {
			inv.setItem(13, getItem(Mat.GOLD_INGOT, getMsg("Menu.time.hours", " ", " ")));
			inv.getItem(13).setAmount((int) (heures-heures/10*10));
		}if (minutes/10 == 0) {
			inv.setItem(15, getItem(Mat.BARRIER, getMsg("Menu.time.minutes", " ", " ")));
		}else {
			inv.setItem(15, getItem(Mat.GOLD_NUGGET, getMsg("Menu.time.minutes", " ", " ")));
			inv.getItem(15).setAmount((int) (minutes/10));
		}if (minutes/10*10 == minutes || minutes == -1) {
			inv.setItem(16, getItem(Mat.BARRIER, getMsg("Menu.time.minutes", " ", " ")));
		}else {
			inv.setItem(16, getItem(Mat.GOLD_NUGGET, getMsg("Menu.time.minutes", " ", " ")));
			inv.getItem(16).setAmount((int) (minutes-minutes/10*10));
		}
        inv.setItem(14, getItem(Mat.STICK, ""));
        inv.setItem(11, getItem(Mat.STICK, ""));
		LootchestCommand.editinv.put(p, name.name);
		ItemStack sign = new ItemStack(Mat.SIGN, 1);
		ItemMeta meta = sign.getItemMeta(); 
		if(minutes != -1) {
			meta.setDisplayName("Respawn time: " + jours+" days, " + heures + " hours, " + minutes + " minutes.");
		}
		else {
			meta.setDisplayName("Respawn time: infinite");
		}
		sign.setItemMeta(meta);
    	inv.setItem(22, sign);
		p.openInventory(inv);
	}
	
	public  void invEdit(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.items.name", "[Chest]", name.name));
		inv.setContents(name.inv.getContents());;
		LootchestCommand.editinv.put(p, name.name);
		p.openInventory(inv);
	}
	
	public  void invcopy(Player p, Lootchest chest, int j) {
		int i = 0;
		int nbBoxes = 0;
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, getMsg("Menu.copy.name", " ", " "));
		Set<String> boxes = Main.LootChest.keySet();

		for(String keys : boxes) {
			if(j== 2 && nbBoxes < 53) nbBoxes++;
			else if(j> 2 && nbBoxes < (j*54-(2*(j-1)))-52) nbBoxes++;
			//exempter le coffre actuel de la liste, et si il y a plus de 54 coffres, stopper i à 53 si on doit faire deux pages
			
			else if(!keys.equals(chest.name) && (i!=45 || j==1) && (i!=53 || (boxes.size() -1)<=(j*52+1) ) ){
				String name = Main.LootChest.get(keys).holo.replace("&", "§");
				String effect = config.getData().getString("chests." + keys + ".particle");
				String world;
				if(Bukkit.getWorld(Main.LootChest.get(keys).world) == null) {
					world = "Unloaded world";
				}
				else {
					world = Bukkit.getWorld(Main.LootChest.get(keys).world).getName();
				}
				ItemStack item = getItemWithLore(Material.CHEST, "§6" +keys, "§bHologram: §6" + name + "||§bWorld: §6"+ world + "||§bEffect: §6" + effect);
				inv.setItem(i++, item);
			}
			else if (!keys.equals(chest.name) && i==45) {
				String name = getMsg("Menu.copy.page", "[Number]", j-1+"");
				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i++, item);
				String world;
				if(Bukkit.getWorld(Main.LootChest.get(keys).world) == null) {
					world = "Unloaded world";
				}
				else {
					world = Bukkit.getWorld(Main.LootChest.get(keys).world).getName();
				}
				String name2 = Main.LootChest.get(keys).holo.replace("&", "§");
				String effect = Main.LootChest.get(keys).particle;

				ItemStack item2 = getItemWithLore(Material.CHEST, "§6" +keys, "§bHologram: §6" + name2 + "||§bWorld: §6"+ world + "||§bEffect: §6" + effect);
				inv.setItem(i++, item2);
			}
			else if (!keys.equals(chest.name) && i==53){
				String name = getMsg("Menu.copy.page", "[Number]", (j+1)+"");

				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i, item);
				break;

			}
		}			

		p.openInventory(inv);
        LootchestCommand.editinv.put(p, chest.name);
	}
	
	
	public  void mainInv(Player p, String name) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 36, getMsg("Menu.main.name", " ", " "));
        inv.setItem(4, getItem(Mat.ENDER_CHEST, getMsg("Menu.main.copychest", " ", " ")));
        if(config.getConfig().getBoolean("Particles.enable")) {
        	inv.setItem(11, getItem(Mat.ENDER_EYE, getMsg("Menu.main.particles", " ", " ")));
        }
        inv.setItem(13, getItem(Mat.CHEST, getMsg("Menu.main.content", " ", " ")));
        inv.setItem(15, getItem(Mat.CLOCK, getMsg("Menu.main.respawnTime", " ", " ")));
        inv.setItem(22, getItem(Mat.DIAMOND, getMsg("Menu.main.chances", " ", " ")));
        inv.setItem(28, getEnabled("fall", name));
        inv.setItem(30, getEnabled("respawn_cmd", name));
        inv.setItem(32, getEnabled("respawn_natural", name));
        inv.setItem(34, getEnabled("take_message", name));
        
        new BukkitRunnable() {       
            @Override
            public void run() {
            	p.openInventory(inv);
                LootchestCommand.editinv.put(p, name);
            }                
        }.runTaskLater(Main.getInstance(), 2);
    }
	
	ItemStack getEnabled(String path, String name){
		if(config.getData().getBoolean("chests." + name +  "." + path)) {
			return getItem(Mat.EMERALD_BLOCK, getMsg("Menu.main.disable_" + path, " ", " "));
		}else {
			return getItem(Mat.REDSTONE_BLOCK, getMsg("Menu.main.enable_" + path, " ", " "));
		}
	}
	
	
	public ItemStack switchState(String path, String name){
		if(!config.getData().getBoolean("chests." + name +  "." + path)) {
			config.getData().set("chests." + name +  "." + path, true);
			config.reloadData();

			return getItem(Mat.EMERALD_BLOCK, getMsg("Menu.main.disable_" + path, " ", " "));
		}else {
			config.getData().set("chests." + name + "." + path, false);
			config.reloadData();

			return getItem(Mat.REDSTONE_BLOCK, getMsg("Menu.main.enable_" + path, " ", " "));
		}
	}
	

	
	// /!\ Certains items ne sont pas les m§mes selont que l'on est en 1.12 ou 1.13, § v§rifier pour particules
	public  void particleInv(Player p, String name) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, getMsg("Menu.particles.name", " ", " "));
        inv.setItem(0, getItem(Mat.TNT, "Huge Explosion"));
        inv.setItem(1, getItem(Mat.TNT, "Large Explosion"));
        inv.setItem(2, getItem(Mat.TNT, "Normal Explosion"));
        inv.setItem(3, getItem(Mat.FIREWORK, "Fireworks Sparks"));
        inv.setItem(4, getItem(Mat.PRISMARINE, "Bubble Pop"));
        inv.setItem(5, getItem(Mat.STONE, "Suspended"));
        inv.setItem(6, getItem(Mat.MYCELIUM, "Town Aura"));
        inv.setItem(7, getItem(Mat.IRON_SWORD, "Crit"));
        inv.setItem(8, getItem(Mat.DIAMOND_SWORD, "Magic Crit"));
        inv.setItem(9, getItem(Mat.FURNACE, "Normal Smoke"));
        inv.setItem(10, getItem(Mat.FURNACE, "Large Smoke"));
        inv.setItem(11, getItem(Mat.ENCHANTED_BOOK, "Mob Spell"));
        inv.setItem(12, getItem(Mat.ENCHANTED_BOOK, "Mob Spell Ambient"));
        inv.setItem(13, getItem(Mat.ENCHANTED_BOOK, "Spell"));
        inv.setItem(14, getItem(Mat.ENCHANTED_BOOK, "Instant Spell"));
        inv.setItem(15, getItem(Mat.ENCHANTED_BOOK, "Witch Spell"));
        inv.setItem(16, getItem(Mat.NOTE_BLOCK, "Note"));
        inv.setItem(17, getItem(Mat.END_PORTAL_FRAME, "Portal"));
        inv.setItem(18, getItem(Mat.ENCHANTING_TABLE , "Enchantment Table"));
        inv.setItem(19, getItem(Mat.BLAZE_POWDER, "Flame"));
        inv.setItem(20, getItem(Mat.LAVA_BUCKET, "Lava"));
        inv.setItem(21, getItem(Mat.STONE, "Footstep"));
        inv.setItem(22, getItem(Mat.WATER_BUCKET, "Water Splash"));
        inv.setItem(23, getItem(Mat.WATER_BUCKET, "Water Wake"));
        inv.setItem(24, getItem(Mat.QUARTZ, "Cloud"));
        inv.setItem(25, getItem(Mat.SNOW_BALL, "Snowball"));
        inv.setItem(26, getItem(Mat.WATER_BUCKET, "Drip Water"));
        inv.setItem(27, getItem(Mat.LAVA_BUCKET, "Drip Lava"));
        inv.setItem(28, getItem(Mat.IRON_SHOVEL, "Snow Shovel"));
        inv.setItem(29, getItem(Mat.SLIME_BALL, "Slime"));
        inv.setItem(30, getItem(Mat.ROSE_RED, "Heart"));
        inv.setItem(31, getItem(Mat.REDSTONE_BLOCK, "Angry Villager"));
        inv.setItem(32, getItem(Mat.EMERALD, "Happy Villager"));
        inv.setItem(33, getItem(Mat.BARRIER, "Barrier"));
        p.openInventory(inv);
        LootchestCommand.editinv.put(p, name);
    }


	public  ItemStack getItem(final Material enderChest, final String customName) {
		final ItemStack A = new ItemStack(enderChest, 1);
		final ItemMeta B = A.getItemMeta();
		if (customName != null) {
			B.setDisplayName(customName);
		}
		A.setItemMeta(B);
		return A;
	}
	
	public  ItemStack getItemWithLore(final Material material, final String customName,  String lore) {
		final ItemStack A = new ItemStack(material);
		final ItemMeta B = A.getItemMeta();
		if (customName != null) {
			B.setDisplayName(customName);
			List<String> lore2 = new ArrayList<String>(Arrays.asList(lore.split("\\|\\|")));
			B.setLore(lore2);
		}
		A.setItemMeta(B);
		return A;
	}
	
	
	@SuppressWarnings("deprecation")
	public  String getDirection(Block chest) {
		String data, direction;
		if(Bukkit.getVersion().contains("1.15")) {
		   data = chest.toString();
			direction = data.substring(data.indexOf("facing") + 7, data.indexOf("facing") + 12).toUpperCase();
			if(direction.charAt(4) != 'H') {
				direction = direction.substring(0, 4);
			}
		}
		else {
			data = chest.getState().getData().toString();
			if(data.indexOf(",type")<0) {
				direction = data.substring(data.indexOf("facing") + 7, data.length());
			}
			else {
				direction = data.substring(data.indexOf("facing") + 7, data.indexOf(",type"));
			}
		}
		return direction;
		
	}
	

	

	
	
}
