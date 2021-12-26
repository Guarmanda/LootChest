package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import fr.black_eyes.lootchest.falleffect.FallingPackageEntity;
import fr.black_eyes.lootchest.particles.Particle;

@SuppressWarnings("deprecation")
public class Utils  {
	private Main main;
	private Files configFiles;

	public Utils() {
		main = Main.getInstance();
		configFiles = main.getConfigFiles();
	}
	
	//message functions that automatically get a message from config lang file
	public void msg(CommandSender p, String path, String replacer, String replacement) {
		String message = path;
		if(configFiles.getLang().isSet(path)) {
			message = getMsg(path, replacer, replacement);
		}
		sendMultilineMessage(message, p);
	}
	
	//message functions that automatically get a message from config lang file
	public void msg(CommandSender p, String path, String replacer, String replacement, String replacer2, String replacement2) {
		String message = configFiles.getLang().getString(path).replace(replacer, replacement).replace(replacer2, replacement2);
		sendMultilineMessage(message, p);
	}
	
	/*
	 * This function is only for messages of chest spawning.
	 * 
	 */
	public void msg(CommandSender p, String path,  String r1, String r1b, String r2, String r2b, String r3, String r3b, String r4, String r4b,  String r5, String r5b) {
		String message = path;
		if(configFiles.getLang().isSet(path)) {
			message = configFiles.getLang().getString(path);
		}
		message = message.replace(r1, r1b).replace(r2, r2b).replace(r3, r3b).replace(r4, r4b).replace(r5, r5b);
		sendMultilineMessage(message, p);
	}
	
	private void sendMultilineMessage(String message, CommandSender player) {
		List<String> msgs = Arrays.asList(message.split("\\\\n"));
		msgs.stream().forEach(msg -> player.sendMessage(msg.replace("&", "§")));
	}
	
	public String getMsg(String path, String replacer, String replacement) {
		return configFiles.getLang().getString(path).replace(replacer, replacement);
	}
	
	//function to copy a chest
	//fonction pour copier un coffre
	public void copychest(Lootchest chest1, Lootchest chest2) {
		chest2.setHolo(chest1.getHolo());
		chest2.chances = chest1.chances.clone();
		//chest2.direction = chest1.direction; let's not change original direction
		chest2.setFall(chest1.getFall());
		chest2.getInv().setContents(chest1.getInv().getContents());
		chest2.setTime(chest1.getTime());
		chest2.setParticle(chest1.getParticle());
		chest2.setRespawn_cmd(chest1.getRespawn_cmd());
		chest2.setRespawn_natural(chest1.getRespawn_natural());
		chest2.setTake_msg(chest1.getTake_msg());
		chest2.setRadius(chest1.getRadius());
		updateData(chest2);
		restoreChest(chest2, true);
	}
	

	
	//cr§er le coffe et enregistrer les infos
	//chest creation and registering

	
	
	//fonction pour changer la position d'un coffre
	//function to change a chest location
	public void changepos(Lootchest name, Location loc3) {
		Location loc = name.getActualLocation();
		if(Bukkit.getWorld(name.getWorld()) != null && name.isGoodType(loc.getBlock())) {
			Block chest = loc.getBlock();
			//deleteholo(loc);
			((InventoryHolder) chest.getLocation().getBlock().getState()).getInventory().clear();
			chest.setType(Material.AIR);
			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			loc2.add(0.5,0.5,0.5);
			main.getPart().remove(loc2);
		}
		name.setWorld(loc3.getWorld().getName());
		name.setGlobalLoc(loc3);
		restoreChest(name, true);
	}
	
	//deletes a chest
	//supprimes un coffre
	public void deleteChest(Lootchest lc) {
		lc.getHologram().remove();
		Location loc = lc.getPosition();
		if(lc.getRandomPosition() != null) {
			loc = lc.getRandomPosition();
		}
		Block chest = loc.getBlock();
		if(lc.isGoodType(chest)) {
			((InventoryHolder) chest.getState()).getInventory().clear();
		}
		chest.setType(Material.AIR);
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.add(0.5,0.5,0.5);
		main.getPart().remove(loc2);
		Main.getInstance().getLootChest().remove(lc.getName());
		
	}
	
	public void sheduleRespawn(Lootchest lc) {

		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		long minutes = lc.getTime();
		long tempsenregistre = lc.getLastreset();
		
		if( minutes<1) {
			return;
		}
		new BukkitRunnable() {       
            @Override
            public void run() {
            	restoreChest(lc, false);
            }                
        }.runTaskLater(Main.getInstance(), (minutes*60-((tempsactuel - tempsenregistre)/1000))*20);
    }
	
	//to fill a chest or give chest to player
    public void fillInventory(Lootchest name, final Inventory inv, final boolean clear, final Player p) {
        if (clear) {
            inv.clear();
        }
        for (int i=0; i<27; i++){
        	if(name.getInv().getItem(i) != null && !name.getInv().getItem(i).getType().equals(Material.AIR)) {
	            final ItemStack item = name.getInv().getItem(i);
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
		//if the lootchest isn't here, let's not spawn particles or anything
		if(!lc.isGoodType(loc.getBlock())) {
			return;
		}
		if(Main.configs.UseHologram){
			lc.getHologram().setLoc(loc);
		}
		if(Main.getVersion()!=7 && lc.getFall() && Main.configs.FALL_Let_Block_Above_Chest_After_Fall){
			Location arm = loc.clone();
			arm.add(0.5, 2, 0.5);
			Material mat = Material.valueOf(Main.configs.FALL_Block);
			Entity ent = (org.bukkit.entity.ArmorStand) loc.getWorld().spawnEntity(arm, org.bukkit.entity.EntityType.ARMOR_STAND);
			
			
			((org.bukkit.entity.ArmorStand) ent).setVisible(false); //Makes the ArmorStand invisible
		 	((org.bukkit.entity.ArmorStand) ent).setHelmet(new ItemStack(mat, 1));
	        if (Main.getVersion()<13) {
			 	if(mat.equals(Material.valueOf("WOOL"))) {
			 		((org.bukkit.entity.ArmorStand) ent).setHelmet(new ItemStack(mat, 1, DyeColor.valueOf(Main.configs.FALL_Optionnal_Color_If_Block_Is_Wool).getDyeData()));
			 	}
		 	}
		 	((org.bukkit.entity.ArmorStand) ent).setBasePlate(false);
		 	((org.bukkit.entity.ArmorStand) ent).setGravity(true);
			
		}
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.add(0.5,0.5,0.5);
		for(Particle part : main.getParticules()) {
			if(lc.getParticle() != null && (""+part).contains(lc.getParticle().name())) {
				main.getPart().put(loc2, part);
			}
		}
		
	}
	
	//gives a random number from -max to max
	private int randomInt(int max) {
		return ThreadLocalRandom.current().nextInt(0-max, max+1);
	}
	
	private Location getRandomLocation(Location center, int radius) {
		center.setX(randomInt(radius)+center.getX());
		center.setZ(randomInt(radius)+center.getZ());
		center.setY(center.getWorld().getHighestBlockYAt(center));
		if (Main.getVersion()>14) {
			center.setY(center.getWorld().getHighestBlockYAt(center)+1);
		}
		return center;
	}
	
	//se sert du config.getData().yml pour set le coffre et remplir son inventaire, cr§er l'holo en fonction du nom 
	//Taking informations from config.getData().yml to restore a specific chest if it is time to do it, or if we force respawn. 
	public boolean restoreChest(Lootchest lc, Boolean force) {
		if(!Main.getInstance().getLootChest().containsValue(lc)) {
			return false;
		}
		Integer num =Main.configs.Minimum_Number_Of_Players_For_Natural_Spawning;
		int players = 0;
		for(@SuppressWarnings("unused") Player p : org.bukkit.Bukkit.getOnlinePlayers()) players++;
		//Main.getInstance().getLogger().info("respawn function of "+ name + " (1)");

		if(Bukkit.getWorld(lc.getWorld()) == null) {
			Bukkit.getLogger().info("§cThe world " + lc.getWorld() + " is not loaded, can't respawn chest " + lc.getName());
			return false;
		}
		
		//Main.getInstance().getLogger().info("respawn function of "+ name + " (2)");

		Location loc = lc.getPosition();
		//if this option is true, we count players in a way that is compatible with all versions, then we take the location of one of these players randomly
		if(Main.configs.use_players_locations_for_randomspawn && lc.getRadius() !=0) {
			int i = 0;
			Iterator<?> itr = Bukkit.getWorld(lc.getWorld()).getPlayers().iterator();
			while( itr.hasNext()) { 
				itr.next(); i++;
			}
			if(i>0) {
				int ran = ThreadLocalRandom.current().nextInt(1, i+1);
				i=0;
				for(Player p : Bukkit.getWorld(lc.getWorld()).getPlayers()) {
					if(++i == ran) {
						loc = p.getLocation().getBlock().getLocation();
					}
					
				}
			}
		}
		Location newloc = loc;
		if(lc.getRadius() != 0) {
			int random = lc.getRadius();
			Location randompos = getRandomLocation(loc, random );

			if(Main.configs.Prevent_Chest_Spawn_In_Protected_Places || Main.configs.WorldBorder_Check_For_Spawn) {
				int counter = 0;
				boolean checkreg = Main.configs.Prevent_Chest_Spawn_In_Protected_Places;
				boolean checkwb = Main.configs.WorldBorder_Check_For_Spawn && randompos.getWorld().getWorldBorder()!=null;
				while(counter<50 && ((checkreg &&ProtectedRegions.isProtected(randompos)) || checkwb && (isOutsideOfBorder(randompos) ))) {
					randompos = getRandomLocation(loc, random );
					counter++;
				}
				if(counter == 50) {
					Bukkit.getLogger().info("§cThe chest " + lc.getName() + " didn't found an unprotected location, so that it can't respawn! " );
					long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
					lc.setLastreset(tempsactuel);
					sheduleRespawn(lc);
					return false;
				}
				
			}

			if(lc.getRandomPosition() != null) {
				loc = lc.getRandomPosition();
			}

			newloc = randompos;


			
			
		}
		final Block block = loc.getBlock();
		
		//Bukkit.getLogger().info("respawn function of "+ name + " (3)");
		final Location loc3 = loc.clone();

		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		long minutes = lc.getTime()*60*1000;
		long tempsenregistre = lc.getLastreset();

		if((tempsactuel - tempsenregistre > minutes && minutes>-1) || force) {
			int height = Main.configs.FALL_Height;
			Location startloc = new Location(newloc.getWorld(), newloc.getX()+0.5, newloc.getY()+height, newloc.getZ()+0.5);
			Boolean loaded = startloc.getWorld().isChunkLoaded((int)startloc.getX()/16, (int)startloc.getZ()/16) ;
			if(lc.getRadius()!=0 && loc3 != newloc && lc.isGoodType(block) ) {
				//deleteholo(loc3);
				((InventoryHolder) block.getState()).getInventory().clear();
				block.setType(Material.AIR);
				loc3.add(0.5,0.5,0.5);
				main.getPart().remove(loc3);
				
			}
			Boolean loaded2 = startloc.getWorld().isChunkLoaded((int)startloc.getX()/16, (int)startloc.getZ()/16) ;
			if(loaded != loaded2) {
				startloc.getWorld().unloadChunk((int)startloc.getX()/16, (int)startloc.getZ()/16);
			}
			
			
			if(lc.getRadius() != 0) {
				lc.setRandomLoc(newloc);
			}
			//Bukkit.getLogger().info("respawn function of "+ lc.name + " force:" + force+" lc.respawn_natural:"+ lc.respawn_natural + " num <= players:"+(num <= players) );
			
			if(!force && lc.getRespawn_natural() && num <= players ) {
				//Bukkit.getLogger().info("entrÃ©e if message");
				String holo = lc.getHolo();
				if(Main.configs.NOTE_bungee_broadcast) {
					BungeeChannel.bungeeBroadcast((((Main.configs.NOTE_natural_msg.replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
				}
				else if(!Main.configs.NOTE_per_world_message) {
					//Bukkit.getLogger().info("per world msg dÃ©sactivÃ©, envoie");
					for(Player p : Bukkit.getOnlinePlayers()) {
						msg(p, Main.configs.NOTE_natural_msg,"[World]", newloc.getWorld().getName(), "[Chest]", holo, "[x]", newloc.getX()+"", "[y]", newloc.getY()+"", "[z]", newloc.getZ()+"");
					}
						
				}else {
					//Bukkit.getLogger().info("per world msg activÃ©, envoie");
					for(Player p : loc.getWorld().getPlayers()){
						msg(p, Main.configs.NOTE_natural_msg, "[World]", newloc.getWorld().getName(), "[Chest]", holo, "[x]", newloc.getX()+"", "[y]", newloc.getY()+"", "[z]", newloc.getZ()+"");
					}
				}
			}

			
			final Location theloc = newloc;
			//Bukkit.getLogger().info("respawn function of "+ name);
			if(lc.getFall() && (num <= players || force) ) {
				
				
				final Block newblock = newloc.getBlock();
				if(loaded || Main.configs.FALL_Let_Block_Above_Chest_After_Fall) {

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
			
			if(!Main.configs.UseHologram && Main.getVersion()!=7){
				lc.getHologram().remove();

			}

			

		}
		else {
			//Main.getInstance().getLogger().info("time: "+ ((new Timestamp(System.currentTimeMillis())).getTime() - tempsenregistre) + " ; defined time: "+ minutes);
			sheduleRespawn(lc);
		}
		
		return ((tempsactuel - tempsenregistre > minutes && minutes>=0) || force);
	}
	
	/**
	 * Checks if location is outside border (thanks spigot forum)
	 * @param the location to check
	 * @return
	 */
	public boolean isOutsideOfBorder(Location loc) {
		org.bukkit.WorldBorder border = loc.getWorld().getWorldBorder();
        double size = border.getSize()/2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }
	
	/**
	 * used by restoreChest, spawns the chest
	 * @param name - A Lootchest to spawn
	 * @param block - The block concerned, where the spawn will append
	 * @param theloc - Location of the block
	 * @param force - True if respawned with a command
	 */
	public  void spawnChest(Lootchest name, Block block, Location theloc, Boolean force) {
		Integer num = Main.configs.Minimum_Number_Of_Players_For_Natural_Spawning;
		int players = getPlayersOnServer();
		block.setType(name.getType());

		if(num <= players || force) {
			Inventory inv = ((InventoryHolder) block.getState()).getInventory();
			fillInventory(name, inv, true, null);

			String direction = name.getDirection();
			MaterialData data = null;
			//if the chest isn't a barrel, we can change its direction
			if( !(Mat.CHEST != Mat.BARREL && name.getType() == Mat.BARREL) && Main.getVersion()!=7) {
				//Main.logInfo("chest direction set to "+direction);
				 data = (DirectionalContainer)block.getState().getData();
				((DirectionalContainer)data).setFacingDirection(BlockFace.valueOf(direction));
				BlockState state = block.getState();
				state.setData(data);
				state.update();
			}
			
			final Location loc2 = name.getActualLocation();
			loc2.add(0.5,0.5,0.5);
			if(name.isGoodType(block) ) {
				if(name.getParticle() == null){
					main.getPart().remove(loc2);
				}
				else{
					for(Particle part : main.getParticules()) {
						if((""+part).contains(name.getParticle().name())) {
							main.getPart().put(loc2, part);
						}
					}
				}
			}else if  (!name.isGoodType(block) ){
				name.getHologram().remove();
				main.getPart().remove(loc2);
			}
			if(Main.configs.UseHologram){
				name.getHologram().setLoc(theloc);
			}
		}
		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		name.setLastreset(tempsactuel);
		if(Main.configs.save_Chest_Locations_At_Every_Spawn) {
			configFiles.reloadData();
		}
		//config.reloadData();
		//Main.getInstance().getLogger().info("Shedule respawn of chest "+ name);
		name.setTaken(false);
		sheduleRespawn(name);
	}
	
	/**
	 * This function was added really recently to spigot. This comes directly from spigot sources. 
	 * I needed it for all versions, so I had to put it here
	 * @param yaw - A plaer yaw that wasn't normalized yet
	 * @return a normalized yaw, that allows me to get the player's direction
	 */
	public static float normalizeYaw(float yaw) {
        yaw %= 360.0f;
        if (yaw >= 180.0f) {
            yaw -= 360.0f;
        } else if (yaw < -180.0f) {
            yaw += 360.0f;
        }
        return yaw;
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
		
		for(Lootchest keys : Main.getInstance().getLootChest().values()) {
			Location loc2 = keys.getActualLocation();
			if(loc2.equals(loc)) {
				return keys;
			}
		}
		return null;
	}
	
	//geting chest position from config.getData().yml
	public  Location getPosition(String name) {
		if (configFiles.getData().getString("chests." + name + ".position.world") == null) {
			main.getLogger().info("§cThe plugin couldn't get the world of chest §6" + name +"§c. This won't prevent the plugin to work, but the plugin may throw other errors because of that.");
			return null;
		}
		World world = Bukkit.getWorld(configFiles.getData().getString("chests." + name + ".position.world"));
		double x = configFiles.getData().getDouble("chests." + name + ".position.x");
		double y = configFiles.getData().getDouble("chests." + name + ".position.y");
		double z = configFiles.getData().getDouble("chests." + name + ".position.z");
		float pitch = (float) configFiles.getData().getDouble("chests." + name + ".position.pitch");
		float yaw = (float) configFiles.getData().getDouble("chests." + name + ".position.yaw");
		return new Location(world, x, y, z, pitch, yaw);
	}
	
	//seting chest position in config.getData().yml
	public  void setPosition(String name, Location loc) {
		configFiles.getData().set("chests." + name + ".position.world", loc.getWorld().getName());
		configFiles.getData().set("chests." + name + ".position.x", loc.getX());
		configFiles.getData().set("chests." + name + ".position.y", loc.getY());
		configFiles.getData().set("chests." + name + ".position.z", loc.getZ());
		configFiles.getData().set("chests." + name + ".position.pitch", loc.getPitch());
		configFiles.getData().set("chests." + name + ".position.yaw", loc.getYaw());
	}
	
	public  void setRandomPosition(String name, Location loc) {
		try {
		String world = loc.getWorld().getName();
		configFiles.getData().set("chests." + name + ".randomPosition.world", world);
		configFiles.getData().set("chests." + name + ".randomPosition.x", loc.getX());
		configFiles.getData().set("chests." + name + ".randomPosition.y", loc.getY());
		configFiles.getData().set("chests." + name + ".randomPosition.z", loc.getZ());
		configFiles.getData().set("chests." + name + ".randomPosition.pitch", loc.getPitch());
		configFiles.getData().set("chests." + name + ".randomPosition.yaw", loc.getYaw());
		}catch(NullPointerException e) {
			main.getLogger().info(name + " " +loc.toString());
		}
	}
	
	public void updateData() {
		for(Lootchest lc : Main.getInstance().getLootChest().values()) {
			lc.saveInConfig();
		}
		configFiles.saveData();
	}
	
	public void updateData(Lootchest lc) {
		lc.saveInConfig();
		configFiles.saveData();
	}
	
	public int killOldHolograms(boolean all) {
		int cpt = 0;
		List<String> holograms = new ArrayList<String>();
		Main.getInstance().getLootChest().values().forEach(lc -> {
			lc.getActualLocation().getWorld().loadChunk(lc.getActualLocation().getChunk());
			holograms.add(lc.getHolo().replace("&", "§"));
		});
		for(World world : Bukkit.getWorlds()) {
			for(Entity ent : world.getEntities()) {
				if(ent instanceof org.bukkit.entity.ArmorStand) {
					//remove is set to true if the entity name contains hologram text
					boolean remove = false;
					for(String holo:holograms) {
						if(ent.getCustomName() != null && holo !=null && ent.getCustomName().contains(holo)) {
							remove = true;
							break;
						}
					}
					Location loc = ent.getLocation();
					loc = loc.subtract(0, Main.configs.Hologram_distance_to_chest, 0);
					//if the block under holo is a chest or if we want to remove all holo (at startup)
					if((!Mat.isALootChestBlock(loc.getBlock()) || all) &&remove) {
						ent.remove();
						cpt++;
					}
				}
			}
		}
		if(all) {
			main.logInfo("Removed "+cpt+" holograms");
		}
		return cpt;
	}
	
	public  Location getRandomPosition(String name) {
		if(!configFiles.getData().isSet("chests." + name + ".randomPosition.x")) {
			return null;
		}
		World world = Bukkit.getWorld(configFiles.getData().getString("chests." + name + ".randomPosition.world"));
		double x = configFiles.getData().getDouble("chests." + name + ".randomPosition.x");
		double y = configFiles.getData().getDouble("chests." + name + ".randomPosition.y");
		double z = configFiles.getData().getDouble("chests." + name + ".randomPosition.z");
		float pitch = (float) configFiles.getData().getDouble("chests." + name + ".randomPosition.pitch");
		float yaw = (float) configFiles.getData().getDouble("chests." + name + ".randomPosition.yaw");
		return new Location(world, x, y, z, pitch, yaw);
	}
	/*
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
	}*/
	
	public int getPlayersOnServer() {
		int players;
		if(Main.getVersion()==7) {
			players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
		}else {
			players = org.bukkit.Bukkit.getOnlinePlayers().size();
		}
		return players;
	}
	
	public int numberOfPlayersAroundLocation(Location loc, int radius) {
		int cpt = 0;
		for (Player players : loc.getWorld().getPlayers()) 
            if (loc.distanceSquared(players.getLocation()) <= radius) 
                cpt++;
        return cpt;    
	}

	
	public List<Player> getPlayersAroundLocation(Location loc, int radius) {
		List<Player> list = new ArrayList<Player>();
		for (Player players : loc.getWorld().getPlayers()) 
            if (loc.distanceSquared(players.getLocation()) <= radius) 
                list.add(players);
        return list;    
	}
	
	/**
	 * Get the direction of a chest with its beautiful superclass "DirectionalContainer"
	 * @param chest a chest Block
	 * @return the direction of the chest
	 */
	public  String getDirection(Block chest) {
		if(Main.getVersion() ==7) {
			return "NORTH";
		}
		MaterialData data = (DirectionalContainer)chest.getState().getData();
		return ((DirectionalContainer)data).getFacing().name();	
	}
	

	

	
	
}
