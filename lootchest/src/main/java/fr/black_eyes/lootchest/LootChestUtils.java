package fr.black_eyes.lootchest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import fr.black_eyes.simpleJavaPlugin.Files;
import fr.black_eyes.simpleJavaPlugin.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class LootChestUtils  {
	private final Files configFiles;

	public LootChestUtils() {
		configFiles = Main.getInstance().getConfigFiles();
	}
	

	
	/**
	* function to copy a chest
	* fonction pour copier un coffre
	* @param chest1 the chest to copy
	* @param chest2 the chest to copy to
	*/
	public static void copychest(Lootchest chest1, Lootchest chest2) {
		chest2.despawn();
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
		chest2.spawn(true);
	}

	
	/**
	 * choose a player location to spawn a chest. If no players online, return null
	 * @param world the LootChest's world
	 */
	public static Location chooseRandomPlayer(String world){
		int i = Bukkit.getWorld(world).getPlayers().size();
		Location globalLoc = null;
		if(i>0) {
			int ran = ThreadLocalRandom.current().nextInt(1, i+1);
			i=0;
			for(Player p : Bukkit.getWorld(world).getPlayers()) {
				if(++i == ran) {
					return p.getLocation().getBlock().getLocation();
				}
			}
		}
		return globalLoc;
	}

	/**
	 * Choose a random location around a starting location that respects certain parameters
	 * @param startingLoc the starting location
	 * @param radius the radius
	 * @return a random location around the starting location
	 */
	public static Location chooseRandomLocation(Location startingLoc, int radius){
		int counter = 0;
		boolean checkProtectedBlock = Main.configs.Prevent_Chest_Spawn_In_Protected_Places;
		boolean checkWorldBorder = Main.configs.WorldBorder_Check_For_Spawn;
		boolean checkWater = !Main.configs.allow_spawning_on_water;
		Location spawnLoc = getRandomLocation(startingLoc, radius );
		while(counter<50 && ((checkProtectedBlock &&ProtectedRegions.isProtected(spawnLoc)) || (checkWater && spawnLoc.getBlock().isLiquid()) || checkWorldBorder && (isOutsideOfBorder(spawnLoc) ))) {
			spawnLoc = getRandomLocation(startingLoc, radius );
			counter++;
		}
		if(counter == 50) {

			return null;
		}else return spawnLoc;
	}
	
	/**
	 * Schedule a chest to respawn
	 * Do several checks to verify that a chest should respawn or not
	 * @param lc
	 */
	public static void scheduleReSpawn(Lootchest lc) {
		long tempsActuel = (new Timestamp(System.currentTimeMillis())).getTime();
		long minutes = lc.getTime();
		if(minutes == 0) {
			lc.spawn(false);
			return;
		}
		if( minutes<0) {
			return;
		}
		long tempsEnregistre = lc.getLastReset();
		long time_to_wait = (minutes*60-((tempsActuel - tempsEnregistre)/1000));
		//if chest should already have respawned (eg it failed to spawn and send us here), let's retry in 30 seconds
		if(time_to_wait<0) {
			time_to_wait = 30;
		}
		// a pull request said that this was suposed to solve respawn problem. I don't experience this bug so I don't know it it works or not
		// plus, this isn't logical at all. but... let's try
		time_to_wait +=5;

		//we have to noe duplicate respawn tasks
		if(lc.getRespawnTask()!=null)
			lc.getRespawnTask().cancel();
		lc.setRespawnTask( new BukkitRunnable() { 	
            @Override
            public void run() {
            	lc.spawn(false);
            }                
        });
		lc.getRespawnTask().runTaskLater(Main.getInstance(), time_to_wait*20);
    }
	
	/**
	 * Fill an inventory with items from a lootchest or to give a lootchest to a player
	 * If player inv is full, drops the items on the ground
	 * @param lc the Lootchest
	 * @param inv the Inventory
	 * @param clear boolean to say if the inventory should be cleared before filling
	 * @param p the Player to give the lootchest to, if null, the lootchest is put in the inventory
	 */
    public static void fillInventory(Lootchest lc, final Inventory inv, final boolean clear, final Player p) {
        if (clear) {
            inv.clear();
        }
		List<Integer> full_slots = new ArrayList<>();

		//&& (lc.getMaxFilledSlots() == 0 || lc.getMaxFilledSlots() > full_slots)
        for (int i=0; i<27 ; i++){
        	if(lc.getInv().getItem(i) != null && !lc.getInv().getItem(i).getType().equals(Material.AIR)) {
	            final ItemStack item = lc.getInv().getItem(i);
	            final int slot = i;
	            final int percent = ThreadLocalRandom.current().nextInt(0, 101);
	            if (percent <= lc.chances[i]) {
					full_slots.add(slot);
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

		//while full_slots >= lc.getMaxFilledSlots(), we remove a random item from the chest
		if(lc.getMaxFilledSlots() > 0) {
			while(full_slots.size() > lc.getMaxFilledSlots()) {
				int index = ThreadLocalRandom.current().nextInt(0, full_slots.size());
				int slot = full_slots.get(index);
				if(inv.getItem(slot) != null && inv.getItem(slot).getType() != Material.AIR) {
					inv.setItem(slot, new ItemStack(Material.AIR));
					full_slots.remove(index);
				}
			}
		}
    }
	
	

	/**
	 * Get the name of a menu from the config file
	 * @param name
	 * @param replacement
	 * @return
	 */
	public static String getMenuName(String name, String replacement) {
		String menuName = Utils.getMsg("Menu."+name+".name", "[Chest]", replacement);
		//cut it to 32 chars max 
		if(menuName.length()>32) {
			menuName = menuName.substring(0, 32);
		}
		return menuName;
	}
	
	/**
	 * Get a random integer between 0 and max
	 * @param max
	 * @return a random integer between 0 and max
	 */
	private static int randomInt(int max) {
		return ThreadLocalRandom.current().nextInt(0-max, max+1);
	}

	/**
	 * Get a random integer between min and max
	 * @param min 
	 * @param max
	 * @return a random integer between min and max
	 */
	public static int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max+1);
	}
	
	/**
	 * Get a random location around a location with a radius
	 * @param startLocation the location to start from
	 * @param radius the radius
	 * @return a random Location around the startLocation
	 */
	public static Location getRandomLocation(Location startLocation, int radius) {
		Location center = startLocation.clone();
		center.setX(randomInt(radius)+center.getX());
		center.setZ(randomInt(radius)+center.getZ());
		center.setY(center.getWorld().getHighestBlockYAt(center));
		if (Main.getCompleteVersion()>=1150) {
			center.setY(center.getWorld().getHighestBlockYAt(center)+1);
		}
		return center;
	}
	
	/**
	 * Checks if location is outside border (thanks spigot forum)
	 * @param the location to check
	 * @return
	 */
	public static boolean isOutsideOfBorder(Location loc) {
		if(loc.getWorld().getWorldBorder()==null) return false;
		
		org.bukkit.WorldBorder border = loc.getWorld().getWorldBorder();
        double size = border.getSize()/2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }
	
	/**
	 * This function was added really recently to spigot. This comes directly from spigot sources. 
	 * I needed it for all versions, so I had to put it here
	 * @param yaw - A player yaw that wasn't normalized yet
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
	
	/**
	 * Check if an inventory is empty
	 * @param inv the inventory to check
	 * @return true if the inventory is empty
	 */
	public static boolean isEmpty(Inventory inv) {
	     for (ItemStack item : inv.getContents() ) {
	       if ( item != null ) {
	         return false;
	       }
	     }
	     return true;
	}

	/**
	 * Check if a location is a lootchest by checking if it's in the lootchest location list
	 * @param loc the location to check
	 * @return the Lootchest object if it's a lootchest, null otherwise
	 */
	public static Lootchest isLootChest(Location loc) {
		for(Lootchest keys : Main.getInstance().getLootChest().values()) {
			Location loc2 = keys.getActualLocation();
			if(loc2.equals(loc)) {
				return keys;
			}
		}
		return null;
	}

	/**
	 * Check if a world name is loaded
	 * @param world
	 * @return true if the world is loaded
	 */
	public static boolean isWorldLoaded(String world){
		for(World w : Bukkit.getWorlds()){
			if(w.getName().equals(world)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Broadcast function for 1.7+ compatibility
	 * @param message
	 */
	public static void broadcast(String message) {
		for(World w : Bukkit.getWorlds()){
			for(Player p : w.getPlayers()) {
				p.sendMessage(Utils.color(message));
			}
		}
		//send message to even console
		Utils.logInfo(message);

	}
	
	/**
	 * Get the position of a chest from the data.yml file
	 * @param name the name of the chest
	 * @return the Location of the chest
	 */
	public  Location getPosition(String name) {
		if (configFiles.getData().getString("chests." + name + ".position.world") == null) {
			Utils.logInfo("&cThe plugin couldn't get the world of chest &6" + name +"&c. This won't prevent the plugin to work, but the plugin may throw other errors because of that.");
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
	
	/**
	 * Set the position of a chest in the data.yml file
	 * @param name the name of the chest
	 * @param loc the location
	 */
	public  void setPosition(String name, Location loc) {
		configFiles.getData().set("chests." + name + ".position.world", loc.getWorld().getName());
		configFiles.getData().set("chests." + name + ".position.x", loc.getX());
		configFiles.getData().set("chests." + name + ".position.y", loc.getY());
		configFiles.getData().set("chests." + name + ".position.z", loc.getZ());
		configFiles.getData().set("chests." + name + ".position.pitch", loc.getPitch());
		configFiles.getData().set("chests." + name + ".position.yaw", loc.getYaw());
	}
	
	/**
	 * Set the actual random position of a chest in the data.yml file
	 * @param name the name of the chest 
	 * @param loc the location
	 */
	public void setRandomPosition(String name, Location loc) {
		try {
		String world = loc.getWorld().getName();
		configFiles.getData().set("chests." + name + ".randomPosition.world", world);
		configFiles.getData().set("chests." + name + ".randomPosition.x", loc.getX());
		configFiles.getData().set("chests." + name + ".randomPosition.y", loc.getY());
		configFiles.getData().set("chests." + name + ".randomPosition.z", loc.getZ());
		configFiles.getData().set("chests." + name + ".randomPosition.pitch", loc.getPitch());
		configFiles.getData().set("chests." + name + ".randomPosition.yaw", loc.getYaw());
		}catch(NullPointerException e) {
			Utils.logInfo(name + " " +loc.toString());
		}
	}
	
	/**
	 * Save all chests in the data.yml file, saves the data.yml file
	 */
	public static void saveAllChests() {
		for(Lootchest lc : Main.getInstance().getLootChest().values()) {
			lc.saveInConfig();
		}
		Main.getInstance().getConfigFiles().saveData();
	}
	

	/**
	 * Get the actual random position of a chest from the data.yml file
	 * (actual random location means the chest has random position enabled, 
	 * and we want to get its last spawn location, where it should actually be)
	 * @param name the name of the chest
	 * @return the actual random Location of the chest
	 */
	public Location getRandomPosition(String name) {
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

	/**
	 * Get the number of players online by adding all players from all worlds
	 * This method is compatible with any version
	 * @return an int of the number of Player(s) online
	 */
	public static int getPlayerCount() {
		int players = 0;
		for(World w : Bukkit.getWorlds())
			players+=  w.getPlayers().size();
		return players;
	}

	/**
	 * Get all players online
	 * This method was made for version compatibility reasons (1.7-1.21)
	 * @return a list of all players online
	 */
	public static List<Player> getPlayersOnline() {
		List<Player> players = new ArrayList<>();
		for(World w : Bukkit.getWorlds())
			players.addAll(w.getPlayers());
		return players;
	}
	
	/**
	 * Get the number of players around a location
	 * @param loc the location
	 * @param radius the radius
	 * @return an int of the number of Player(s) around the location
	 */
	public static int numberOfPlayersAroundLocation(Location loc, int radius) {
		int cpt = 0;
		for (Player players : loc.getWorld().getPlayers()) 
            if (loc.distanceSquared(players.getLocation()) <= radius) 
                cpt++;
        return cpt;    
	}

	/**
	 * Get all players around a location
	 * @param loc the location
	 * @param radius the radius
	 * @return a List of all Player(s) around the location
	 */
	public static List<Player> getPlayersAroundLocation(Location loc, int radius) {
		List<Player> list = new ArrayList<>();
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
	public static String getDirection(Block chest) {
		if(Main.getVersion() ==7) {
			return "NORTH";
		}
		MaterialData data = chest.getState().getData();
		return ((DirectionalContainer)data).getFacing().name();	
	}
	

	

	
	
}
