package fr.black_eyes.lootchest;

import java.sql.Timestamp;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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
		chest2.take_msg = chest1.take_msg;
		chest2.radius = chest1.radius;
		updateData(chest2);
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
        }.runTaskLater(Main.getInstance(), (minutes*60-((tempsactuel - tempsenregistre)/1000))*20+5*20);
    }
	
	//to fill a chest or give chest to player
    public void fillInventory(Lootchest name, final Inventory inv, final boolean clear, final Player p) {
        if (clear) {
            inv.clear();
        }
        for (int i=0; i<26; i++){
        	if(name.inv.getItem(i) != null && !name.inv.getItem(i).getType().equals(Material.AIR)) {
	            final ItemStack item = name.inv.getItem(i);
	            final int slot = i;
	            final int percent = ThreadLocalRandom.current().nextInt(0, 101);
	            if (percent <= name.chances[i]) {
	            	
	                if (inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR) {
	                    inv.setItem(slot, item);
	                }
	                else if (p != null && p.getInventory().firstEmpty() == -1) {
	                    p.getWorld().dropItem(p.getLocation(), item);
	                }
	                else {
	                    inv.addItem(new ItemStack[] { item });
	                }
	            }
        	}
        }
    }
	
	
	public void reactivateEffects(Lootchest lc) {
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
		if(!Main.LootChest.containsValue(lc)) {
			return false;
		}
		Integer num = config.getConfig().getInt("Minimum_Number_Of_Players_For_Natural_Spawning");
		int players = 0;
		if(org.bukkit.Bukkit.getVersion().contains("1.7")) {
			players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
		}else {
			players = org.bukkit.Bukkit.getOnlinePlayers().size();
		}
		//Main.getInstance().getLogger().info("respawn function of "+ name + " (1)");

		if(Bukkit.getWorld(lc.world) == null) {
			Bukkit.getLogger().info("§cThe world " + lc.world + " is not loaded, can't respawn chest " + lc.name);
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
			if(config.getConfig().getBoolean("Prevent_Chest_Spawn_In_Protected_Places")) {
				int counter = 0;
				while(counter<50 && ProtectedRegions.isProtected(randompos)) {
					randompos.setX(randomInt(random)+loc.getX());
					randompos.setZ(randomInt(random)+loc.getZ());
					randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos));
					if (Bukkit.getVersion().contains("1.15")) {
						randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos)+1);
					}
					counter++;
				}
				if(counter == 50) {
					Bukkit.getLogger().info("§cThe chest " + lc.name + " didn't found an unprotected location, so that it can't respawn! " );
					long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
					lc.lastreset = tempsactuel;
					sheduleRespawn(lc);
					return false;
				}
				
			}

			if(lc.randomLoc != null) {
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
			int height = Main.getInstance().getConfig().getInt("Fall_Effect.Height");
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
					for(Player p : Bukkit.getOnlinePlayers()) {
						p.sendMessage((((Main.getInstance().getConfig().getString("respawn_notify.natural_respawn.message").replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
					}
						
				}else {
					for(Player p : loc.getWorld().getPlayers()){
						p.sendMessage((((Main.getInstance().getConfig().getString("respawn_notify.natural_respawn.message").replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
					}
				}
			}

			
			final Location theloc = newloc;
			//Bukkit.getLogger().info("respawn function of "+ name);
			if(lc.fall&& (num <= players || force) ) {
				
				Location startloc = new Location(newloc.getWorld(), newloc.getX()+0.5, newloc.getY()+height, newloc.getZ()+0.5);
				Boolean loaded = startloc.getWorld().isChunkLoaded(startloc.getBlockX()/16, startloc.getBlockZ()/16) ;
				final Block newblock = newloc.getBlock();
				if(loaded || config.getConfig().getBoolean("Fall_Effect.Let_Block_Above_Chest_After_Fall")) {

					new FallingPackageEntity(startloc, loaded, theloc);

		                	
		                	spawnChest(lc, newblock, theloc, force);

				}else {
					spawnChest(lc, newblock, theloc, force);
				}

				
			}
			else {
				final Block newblock = newloc.getBlock();
				spawnChest(lc, newblock, theloc, force);
			}
			
			if(!Main.getInstance().getConfig().getBoolean("UseHologram")){
				deleteholo(loc);

			}

			

		}
		else {
			//Main.getInstance().getLogger().info("time: "+ ((new Timestamp(System.currentTimeMillis())).getTime() - tempsenregistre) + " ; defined time: "+ minutes);
			sheduleRespawn(lc);
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
				if(name.particle.equals("Disabled")){
					Main.part.remove(loc2);
				}
				else{
					for(Object part : Main.particules) {
						if((""+part).contains(name.particle)) {
							Main.part.put(loc2, part);
						}
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
			for(Entity ent : loc2.getChunk().getEntities()) {
				if(ent instanceof org.bukkit.entity.ArmorStand && ent.getLocation().distance((loc2.clone()).add(0,1,0)) < 1) {
					if(!(( org.bukkit.entity.ArmorStand) ent).isVisible()) ent.remove();
				}
			}
			loc2.add(0.5, Main.getInstance().getConfig().getInt("Hologram_distance_to_chest"), 0.5);
			//the coordinates of a block are at the corner of the block
			for(Entity ent : loc2.getChunk().getEntities()) {
				if(ent instanceof org.bukkit.entity.ArmorStand && ent.getLocation().distance(loc2) <1) {
					if(!(( org.bukkit.entity.ArmorStand) ent).isVisible()) ent.remove();
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
