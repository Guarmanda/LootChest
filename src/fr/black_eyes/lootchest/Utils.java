package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.Iterator;
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
		p.sendMessage(getMsg(path, replacer, replacement));
	}
	
	public String getMsg(String path, String replacer, String replacement) {
		return configFiles.getLang().getString(path).replace(replacer, replacement).replace("&", "§");
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
			deleteholo(loc);
			((InventoryHolder) chest.getLocation().getBlock().getState()).getInventory().clear();
			chest.setType(Material.AIR);
			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			loc2.setX(loc.getX()+0.5);
			loc2.setY(loc.getY()+0.5);
			loc2.setZ(loc.getZ()+0.5);
			main.getPart().remove(loc2);
		}
		name.setWorld(loc3.getWorld().getName());
		name.setGlobalLoc(loc3);
		restoreChest(name, true);
	}
	
	//deletes a chest
	//supprimes un coffre
	public void deleteChest(Lootchest lc) {
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
		loc2.setX(loc.getX()+0.5);
		loc2.setY(loc.getY()+0.5);
		loc2.setZ(loc.getZ()+0.5);
		main.getPart().remove(loc2);
		deleteholo(chest.getLocation());
		Main.getInstance().getLootChest().remove(lc.name);
		
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
		if(!lc.isGoodType(loc.getBlock())) {
			return;
		}
		if(Main.getInstance().getConfig().getBoolean("UseHologram")){
			deleteholo(loc);
			makeHolo(loc, lc);
		}
		if(!Bukkit.getVersion().contains("1.7") && lc.getFall() && Main.configs.FALL_Let_Block_Above_Chest_After_Fall){
			Location arm = loc.clone();
			arm.add(0.5, 2, 0.5);
			Material mat = Material.valueOf(Main.configs.FALL_Block);
			Entity ent = (org.bukkit.entity.ArmorStand) loc.getWorld().spawnEntity(arm, org.bukkit.entity.EntityType.ARMOR_STAND);
			
			
			((org.bukkit.entity.ArmorStand) ent).setVisible(false); //Makes the ArmorStand invisible
		 	((org.bukkit.entity.ArmorStand) ent).setHelmet(new ItemStack(mat, 1));
		 	if(!Bukkit.getVersion().contains("1.13") && !Bukkit.getVersion().contains("1.14") && !Bukkit.getVersion().contains("1.15") && !Bukkit.getVersion().contains("1.16")) {
			 	if(mat.equals(Material.valueOf("WOOL"))) {
			 		((org.bukkit.entity.ArmorStand) ent).setHelmet(new ItemStack(mat, 1, DyeColor.valueOf(Main.configs.FALL_Optionnal_Color_If_Block_Is_Wool).getDyeData()));
			 	}
		 	}
		 	((org.bukkit.entity.ArmorStand) ent).setBasePlate(false);
		 	((org.bukkit.entity.ArmorStand) ent).setGravity(true);
			
		}
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.add(0.5,0.5,0.5);
		for(Object part : main.getParticules()) {
			if((""+part).contains(lc.getParticle())) {
				main.getPart().put(loc2, part);
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
		if(!Main.getInstance().getLootChest().containsValue(lc)) {
			return false;
		}
		Integer num =Main.configs.Minimum_Number_Of_Players_For_Natural_Spawning;
		int players = 0;
		if(org.bukkit.Bukkit.getVersion().contains("1.7")) {
			players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
		}else {
			players = org.bukkit.Bukkit.getOnlinePlayers().size();
		}
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
			Location randompos = lc.getPosition();

			randompos.setX(randomInt(random)+loc.getX());
			randompos.setZ(randomInt(random)+loc.getZ());
			randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos));
			if (Bukkit.getVersion().contains("1.15.2") || Bukkit.getVersion().contains("1.16")) {
				randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos)+1);
			}
			if(Main.configs.Prevent_Chest_Spawn_In_Protected_Places) {
				int counter = 0;
				while(counter<50 && ProtectedRegions.isProtected(randompos)) {
					randompos.setX(randomInt(random)+loc.getX());
					randompos.setZ(randomInt(random)+loc.getZ());
					randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos));
					if (Bukkit.getVersion().contains("1.15")|| Bukkit.getVersion().contains("1.16")) {
						randompos.setY(randompos.getWorld().getHighestBlockYAt(randompos)+1);
					}
					counter++;
				
				}
				if(counter == 50) {
					Bukkit.getLogger().info("§cThe chest " + lc.name + " didn't found an unprotected location, so that it can't respawn! " );
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
				deleteholo(loc3);
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
				//Bukkit.getLogger().info("entrée if message");
				String holo = lc.getHolo();
				if(Main.configs.NOTE_bungee_broadcast) {
					BungeeChannel.bungeeBroadcast((((Main.configs.NOTE_natural_msg.replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
				}
				else if(!Main.configs.NOTE_per_world_message) {
					//Bukkit.getLogger().info("per world msg désactivé, envoie");
					for(Player p : Bukkit.getOnlinePlayers()) {
						p.sendMessage((((Main.configs.NOTE_natural_msg.replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
					}
						
				}else {
					//Bukkit.getLogger().info("per world msg activé, envoie");
					for(Player p : loc.getWorld().getPlayers()){
						p.sendMessage((((Main.configs.NOTE_natural_msg.replace("[Chest]", holo)).replace("[x]", newloc.getX()+"")).replace("[y]", newloc.getY()+"")).replace("[z]", newloc.getZ()+"").replace("&", "§"));
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
			
			if(!Main.configs.UseHologram){
				deleteholo(loc);

			}

			

		}
		else {
			//Main.getInstance().getLogger().info("time: "+ ((new Timestamp(System.currentTimeMillis())).getTime() - tempsenregistre) + " ; defined time: "+ minutes);
			sheduleRespawn(lc);
		}
		
		return ((tempsactuel - tempsenregistre > minutes && minutes>=0) || force);
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
		int players = 0;
		if(org.bukkit.Bukkit.getVersion().contains("1.7")) {
			players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
		}else {
			players = org.bukkit.Bukkit.getOnlinePlayers().size();
		}
		block.setType(name.getType());

		if(num <= players || force) {
			Inventory inv = ((InventoryHolder) block.getState()).getInventory();
			fillInventory(name, inv, true, null);

			String direction = name.getDirection();
			MaterialData data = null;
			//if the chest isn't a barrel, we can change its direction
			if( !(Mat.CHEST != Mat.BARREL && name.getType() == Mat.BARREL) ) {
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
				if(name.getParticle().equals("Disabled")){
					main.getPart().remove(loc2);
				}
				else{
					for(Object part : main.getParticules()) {
						if((""+part).contains(name.getParticle())) {
							main.getPart().put(loc2, part);
						}
					}
				}
			}else if  (!name.isGoodType(block) ){
				deleteholo(theloc);
				main.getPart().remove(loc2);
			}
			if(Main.configs.UseHologram){
				deleteholo(theloc);
				makeHolo(theloc, name);
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
			loc2.add(0.5, Main.configs.Hologram_distance_to_chest, 0.5);
			//the coordinates of a block are at the corner of the block

			String name = lc.getHolo().replace("&", "§");
			if(!((name.equals("\"\"") || name.equals("\" \"") || name.equals("null") || name.equals("") || name.equals(" ") || name.equals("_") || name.equals("none")))) {
				org.bukkit.entity.ArmorStand as = (org.bukkit.entity.ArmorStand) loc2.getWorld().spawnEntity(loc2, org.bukkit.entity.EntityType.ARMOR_STAND); //Spawn the ArmorStand
				
				as.setCustomName(name); //Set this to the text you want
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
			 	if(Main.configs.Show_Timer_On_Hologram && lc.getTime() != -1) {
			 	new BukkitRunnable() {
			    		public void run() {
			    			long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime()/1000;
			    			long minutes = lc.getTime()*60;
			    			long tempsenregistre = lc.getLastreset()/1000;
			    			as.setCustomName(name + " (" + (minutes - (tempsactuel - tempsenregistre)) + ")");
			    			if(minutes - (tempsactuel - tempsenregistre)<=0) {
			    				this.cancel();
			    			}
			    		}
			    	}.runTaskTimer(Main.getInstance(), 0, 20);
			 	}
	
			}
		}
	}	
	

	
	
	
	

	
	/**
	 * Get the direction of a chest with its beautiful superclass "DirectionalContainer"
	 * @param chest a chest Block
	 * @return the direction of the chest
	 */
	public  String getDirection(Block chest) {
		MaterialData data = (DirectionalContainer)chest.getState().getData();
		return ((DirectionalContainer)data).getFacing().name();	
	}
	

	

	
	
}
