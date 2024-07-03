package fr.black_eyes.lootchest;

import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import fr.black_eyes.api.events.LootChestSpawnEvent;
import fr.black_eyes.lootchest.falleffect.FallingPackageEntity;
import fr.black_eyes.lootchest.particles.Particle;
import lombok.Getter;
import lombok.Setter;
public class Lootchest {
	/**
	 * @return the Lootchest name
	 */
	@Getter /**
	 * @param name the name to give to the lootchest
	 */
	@Setter private String name;
	/**
	 * @param globalLoc The static location of the lootchest, which is always used to choose the random location
	 */
	@Setter 		private Location globalLoc;
	/**
	 * @param randomLoc the random location that changes every time a lootchest respawns. Can be disabled to only use global location
	 */
	@Setter 		private Location randomLoc;
	/**
	 * @return The inventory of the lootchest, used to give it to someone, or fill the lootchest
	 */
	@Getter			private Inventory inv;
	/**
	 * @return the value of the fall boolean, which says if the LootChest should display a fall effect or not
	 */
	@Getter /**
	 * @param fall says if the LootChest should display a fall effect or not
	 */
	@Setter private Boolean fall;
	/**
	 * @return An array of integers, representing chances of each item in the chest
	 */
	@Getter			 Integer[] chances;
	/**
	 * @return a string representing the direction of the chest block (north, east, south, or east)
	 */
	@Getter /**
	 * @param direction a string representing the direction of the chest block (north, east, south, or east)
	 */
	@Setter private String direction;
	/**
	 * @return the text displayed by the hologram of the lootchest
	 */
	@Getter private String holo;
	/**
	 * @return the time between two spawns of the lootchest. 
	 */
	@Getter /**
	 * @param time the time between two spawns of the lootchest. -1 to disable auto respawn
	 */
	@Setter private long time;
	/**
	 * @return the last respawn date of the chest, in milliseconds.
	 */
	@Getter /**
	 * @param lastReset the last respawn date of the chest, in milliseconds.
	 */
	private long lastReset;
	/**
	 * @return the particle to spawn around the lootchest
	 */
	@Getter /**
	 * @param particle the particle to spawn around the lootchest
	 */
	@Setter private Particle particle;
	/**
	 * @return the value of respawn_cmd boolean, which says if we should send a broadcast if the chest is respawned manually
	 */
	@Getter /**
	 * @param respawn_cmd boolean, which says if we should send a broadcast if the chest is respawned manually
	 */
	@Setter private Boolean respawn_cmd;
	/**
	 * @return the value of the respawn_natural boolean, which says if we should send a broadcast if the chest is respawned "naturally"
	 */
	@Getter /**
	 * @param respawn_natural boolean, which says if we should send a broadcast if the chest is respawned "naturally"
	 */
	@Setter private Boolean respawn_natural;
	/**
	 * @return the value of the take_msg boolean, which says if we should send a broadcast if the chest taken/looted by a player
	 */
	@Getter /**
	 * @param take_msg boolean, which says if we should send a broadcast if the chest taken/looted by a player
	 */
	@Setter private Boolean take_msg;
	/**
	 * @return the radius around the global location, to set the random location
	 */
	@Getter /** 
	 * @param radius the radius around the global location, to set the random location
	 */
	@Setter private int radius;
	/**
	 * @return the world to spawn the lootchest
	 */
	@Getter /**
	 * @param world the world to spawn the lootchest
	 */
	@Setter private String world;
	/**
	 * @return taken boolean, which says if the chest was looted already or not
	 */
	@Getter /**
	 * @param taken boolean, which says if the chest was looted already or not
	 */
	@Setter private Boolean taken;
	/** 
	 * @return the type of the lootchest, can be chest, trapped chest or barrel
	 */
	 /**
	 * @param type the type of the lootchest, can be chest, trapped chest or barrel
	 */
	@Getter @Setter private Material type;
	/**
	 * @return the hologram object attached to this lootchest
	 */
	@Getter private LootChestHologram hologram;

	@Getter @Setter private long protectionTime;

	@Getter @Setter private BukkitRunnable respawnTask;
	
	@Getter @Setter private Integer maxFilledSlots;
	
	/**
	 * Function used in Main / reload for chest loading
	 * @param naming
	 */
	public Lootchest(String naming) {
		Main main = Main.getInstance();
		Utils utils = main.getUtils();
		Files configFiles = main.getConfigFiles();
		taken = false;
		if(!configFiles.getData().isSet("chests."+naming+".type")){
			type = Mat.CHEST;
		}else {
			String types = configFiles.getData().getString("chests."+naming+".type");
			switch(types) {
				case "TRAPPED_CHEST": type = Mat.TRAPPED_CHEST; break;
				case "BARREL": type = Mat.BARREL; break;
				default: type = Mat.CHEST; break;
			}
		}
		if(!configFiles.getData().isSet("chests."+naming+".maxFilledSlots")){
			maxFilledSlots = Main.configs.default_maxFilledSlots;
		}else{
			maxFilledSlots = configFiles.getData().getInt("chests."+naming+".maxFilledSlots");
		}
		name = naming;
		Integer[] chancesInit = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		chances = chancesInit;
		chancesInit = null;
		inv = Bukkit.createInventory(null, 27);
		globalLoc = utils.getPosition(naming);
		if(configFiles.getData().isSet("chests."+naming+".randomradius")) {
			radius = configFiles.getData().getInt("chests."+naming+".randomradius");
			if(radius > 0) {
				randomLoc = utils.getRandomPosition(naming);
			}else {
				randomLoc = null;
			}
		}else {
			radius = 0;
			randomLoc = null;
		}

		if(configFiles.getData().isSet("chests."+naming+".protectionTime")) {
			protectionTime = configFiles.getData().getLong("chests."+naming+".protectionTime");
		}else {
			protectionTime = Main.configs.defaultRespawnProtection;
		}


		holo = configFiles.getData().getString("chests." + naming + ".holo");
		String part = configFiles.getData().getString("chests." + naming + ".particle");
		if(part.equals("Disabled")) {
			particle = null;
		}else {
			try {
				particle = Particle.valueOf(part);
			}catch(IllegalArgumentException e) {
				particle = Particle.FLAME;
			}
			if(!particle.isSupported()) {
				particle = Particle.FLAME;
			}
		}
		time = configFiles.getData().getInt("chests." + naming + ".time");
		fall =  configFiles.getData().getBoolean("chests." + naming + ".fall");
		try {
		for(String keys : configFiles.getData().getConfigurationSection("chests." + naming + ".inventory").getKeys(false)) {
			inv.setItem(Integer.parseInt(keys), configFiles.getData().getItemStack("chests." + naming + ".inventory." + keys));
			chances[Integer.parseInt(keys)] = configFiles.getData().getInt("chests." + naming + ".chance." + keys);
		}
		}catch(NullPointerException e) {
			main.logInfo("&cMaybe you changed to an older server version recently: chest inventory of "+name+" was lost :/");
		}
		respawn_cmd =  configFiles.getData().getBoolean("chests." + naming + ".respawn_cmd");
		respawn_natural =  configFiles.getData().getBoolean("chests." + naming + ".respawn_natural");
		take_msg =  configFiles.getData().getBoolean("chests." + naming + ".take_message");
		world = configFiles.getData().getString("chests." + naming + ".position.world");
		direction = configFiles.getData().getString("chests." + naming + ".direction");
		lastReset = configFiles.getData().getLong("chests." + name + ".lastreset");
		
		hologram = new LootChestHologram(this);
	}
	
	
	
	/**
	 * Function used for /lc create
	 * @param chest
	 * @param naming
	 */
	public Lootchest(Block chest, String naming){
		type = chest.getType();
		taken = false;
		name = naming;
		inv = Bukkit.createInventory(null, 27);
		Integer[] initChances = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		this.chances = initChances;
		Inventory inventory =  ((InventoryHolder) chest.getState()).getInventory();
		for(int i = 0 ; i < 27 ; i++) {
			if(inventory.getItem(i) != null) {
				inv.setItem( i, inventory.getItem(i));
				chances[i] =  Main.configs.default_item_chance;
			}
		}
		if(inventory.getSize() >27) {
			Main.getInstance().logInfo("&cDo not use double chests to create chests! Only half of the inventory of the chest was registered.");
		}
		maxFilledSlots = Main.configs.default_maxFilledSlots;
		fall =  Main.configs.FALL_Enabled;
		respawn_cmd =  Main.configs.NOTE_command_e;
		respawn_natural =  Main.configs.NOTE_natural_e;
		take_msg =  Main.configs.NOTE_message_on_chest_take;
		if( !(Mat.CHEST != Mat.BARREL && chest.getType() == Mat.BARREL) ) {
			direction = Utils.getDirection(chest);
		}
		holo = name;
		time =  Main.configs.default_reset_time;
		globalLoc =  chest.getLocation();
		lastReset =  new Timestamp(System.currentTimeMillis()).getTime();
	   	particle =  Particle.valueOf(Main.configs.PART_default_particle);
	   	radius = 0;
	   	world = chest.getWorld().getName();
		((InventoryHolder) chest.getLocation().getBlock().getState()).getInventory().clear();
		chest.getLocation().getBlock().setType(Material.AIR);
		protectionTime = Main.configs.defaultRespawnProtection;
		hologram = new LootChestHologram(this);
	}
	
	public Lootchest(Lootchest lc, String name){
		type = lc.getType();
		taken = false;
		this.name = name;
		inv = Bukkit.createInventory(null, 27);
		Integer[] initChances = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		this.chances = initChances;
		for (int i = 0; i < 27; i++) {
			if (lc.getInv().getItem(i) != null) {
				inv.setItem(i, lc.getInv().getItem(i));
				chances[i] = lc.getChances()[i];
			}
		}
		chances = lc.getChances().clone();
		maxFilledSlots = lc.getMaxFilledSlots();
		fall = lc.getFall();
		respawn_cmd = lc.getRespawn_cmd();
		respawn_natural = lc.getRespawn_natural();
		take_msg = lc.getTake_msg();
		direction = lc.getDirection();
		holo = lc.getHolo();
		time = lc.getTime();
		globalLoc = lc.getPosition();
		particle = lc.getParticle();
		radius = lc.getRadius();
		world = lc.getWorld();
		protectionTime = lc.getProtectionTime();
		hologram = new LootChestHologram(this);
	}
	
	/**
	 * Function used at defined time in config and at plugin stop for saving chests. 
	 * This function doesn't save the config, file, it just edits the one in memory. Use "updateData" to save the chest in the file.
	 * UpdateData already calls this function.
	 */
	public void saveInConfig(){
		Main main = Main.getInstance();
		Utils utils = main.getUtils();
		Files configFiles = main.getConfigFiles();
		configFiles.getData().set("chests." + name + ".inventory", null);
		for(int i = 0 ; i < inv.getSize() ; i++) {
			if(inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
				configFiles.getData().set("chests." + name + ".inventory." + i, inv.getItem(i));
				configFiles.getData().set("chests." + name + ".chance." + i, chances[i]);
			}
		}
		configFiles.getData().set("chests." + name + ".fall", fall);
		configFiles.getData().set("chests." + name + ".type", type.name());
		configFiles.getData().set("chests." + name + ".respawn_cmd", respawn_cmd);
		configFiles.getData().set("chests." + name + ".respawn_natural", respawn_natural);
		configFiles.getData().set("chests." + name + ".take_message", take_msg);
		
		configFiles.getData().set("chests." + name + ".direction", direction);
		configFiles.getData().set("chests." + name + ".holo", holo);
		configFiles.getData().set("chests." + name + ".time", time);
		configFiles.getData().set("chests." + name + ".protectionTime", protectionTime);
		utils.setPosition(name, globalLoc);
		configFiles.getData().set("chests." + name + ".lastreset", lastReset);
		if(particle!=null)
			configFiles.getData().set("chests." +name+ ".particle", particle.name());
		else
			configFiles.getData().set("chests." +name+ ".particle", "Disabled");
		configFiles.getData().set("chests."+name+".randomradius", radius);
		if(randomLoc != null) {
			utils.setRandomPosition(name, randomLoc);
		}
		configFiles.getData().set("chests." + name + ".maxFilledSlots", maxFilledSlots);

	}

	//fonction pour changer la position d'un coffre
	//function to change a chest location
	public void setLocation(Location loc3) {
		despawn();
		setWorld(loc3.getWorld().getName());
		setGlobalLoc(loc3);
		spawn(true);
	}

	/**
	 * Remove the chest block, the hologram, and the particle
	 * If the chunk isn't loaded before doing this, it will be unloaded after.
	 * @return true if the chunk was loaded before, false if it wasn't
	 */
	public boolean despawn(){
		Location startLocation = getActualLocation();
		Boolean loaded = startLocation.getWorld().isChunkLoaded((int)startLocation.getX()/16, (int)startLocation.getZ()/16) ;
		if(Utils.isWorldLoaded(getWorld()) && isGoodType(startLocation.getBlock())) {
			Block chest = startLocation.getBlock();
			((InventoryHolder) chest.getLocation().getBlock().getState()).getInventory().clear();
			chest.setType(Material.AIR);
			Main.getInstance().getPart().remove(getParticleLocation());
			hologram.remove();
		}
		Boolean loaded2 = startLocation.getWorld().isChunkLoaded((int)startLocation.getX()/16, (int)startLocation.getZ()/16) ;
		if(loaded != loaded2) {
			startLocation.getWorld().unloadChunk((int)startLocation.getX()/16, (int)startLocation.getZ()/16);
			return false;
		}
		return true;
	}

	/**
	 * @return whever config option Minimum_Number_Of_Players_For_Natural_Spawning is respected
	 */
	private static boolean checkIfEnoughPlayers(){
		Integer num = Main.configs.Minimum_Number_Of_Players_For_Natural_Spawning;
		int players = Utils.getPlayersOnServer();
		return (players >= num); 
	}

	private boolean checkIfTimeToRespawn(){
		long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
		long minutes = getTime()*60*1000;
		long tempsenregistre = getLastReset();
		return (tempsactuel - tempsenregistre > minutes && minutes>-1);
	}

	/**
	 * used by spawn, spawns the chest
	 * @param name - A Lootchest to spawn
	 * @param block - The block concerned, where the spawn will append
	 * @param blockLocation - Location of the block
	 * @param force - True if respawned with a command
	 */
	public void createchest( Block block, Location blockLocation) {
		block.setType(getType());
		Inventory inventory = ((InventoryHolder) block.getState()).getInventory();
		Utils.fillInventory(this, inventory, true, null);
		MaterialData data = null;
		//if the chest isn't a barrel, we can change its direction
		if( !(Mat.CHEST != Mat.BARREL && getType() == Mat.BARREL) && Main.getVersion()>7) {
				data = block.getState().getData();
			((DirectionalContainer)data).setFacingDirection(BlockFace.valueOf(direction));
			BlockState state = block.getState();
			state.setData(data);
			state.update();
		}
		
		// spawn particles and hologram if needed
		final Location loc2 = getParticleLocation();
		if(getParticle() != null && Main.configs.PART_enable){
			for(Particle part : Main.getInstance().getSupportedParticles()) {
				if((""+part).contains(getParticle().name())) {
					Main.getInstance().getPart().put(loc2, part);
				}
			}
		}
		getHologram().setLoc(blockLocation);
		
		setLastReset();
		if(Main.configs.save_Chest_Locations_At_Every_Spawn) {
			saveInConfig();
			Main.getInstance().getConfigFiles().saveData();
		}
		setTaken(false);
		if(protectionTime >0){
			Long now = (new Timestamp(System.currentTimeMillis())).getTime();
			Main.getInstance().getProtection().put(block.getLocation(), now+protectionTime*1000);
		}
		Bukkit.getPluginManager().callEvent(new LootChestSpawnEvent(this));
		Utils.scheduleReSpawn(this);
	}

	/**
	 * Executes the spawn function, despawning the chest only if we force it to respawn
	 * @param forceRespawn Forces the chest to respawn, even if it's not time to respawn
	 */
	public boolean spawn(Boolean forceRespawn){
		if (forceRespawn){
			return spawn(forceRespawn, true);
		}
		else
			return spawn(forceRespawn, false);
	}

	/**
	 * Spawns the chest, with its hologram and particles, checking if it's time to respawn, 
	 * if there's enough players, if the world is loaded, finding a good location, etc.
	 * 
	 * @param forceSpawn Forces the chest to respawn, even if it's not time to respawn
	 * @param forceDespawn Forces the chest to despawn, even if it's not time to respawn
	 * @return
	 */
	public boolean spawn(Boolean forceSpawn, Boolean forceDespawn) {
		if(forceDespawn) {
			despawn();
			setLastReset();
		}
		// if world is not loaded || lootchest was deleted || not enough players
		if(!Utils.isWorldLoaded(getWorld()) || !Main.getInstance().getLootChest().containsValue(this) ) {
			Utils.scheduleReSpawn(this);
			return false;
		}
		// if (there's not enough player || it's not time to respawn) && we didn't force respawn
		if( !checkIfTimeToRespawn() && !forceSpawn) {
			Utils.scheduleReSpawn(this);
			return false;
		}
		if(!checkIfEnoughPlayers() && !forceSpawn) {
			Utils.scheduleReSpawn(this);
			return false;
		}
		Location actualLocation = getActualLocation();
		Boolean chunk_was_loaded = actualLocation.getWorld().isChunkLoaded((int)actualLocation.getX()/16, (int)actualLocation.getZ()/16) ;
		
		Location globalLocation = getPosition();
		Location spawnLoc = globalLocation.clone();
		//if randomSpawn is enabled, we get a random location in the radius
		if(getRadius() !=0){
			//if this option is true, we take the location of one of online players randomly.
			if(Main.configs.use_players_locations_for_randomspawn ) {
				globalLocation = Utils.chooseRandomPlayer(getWorld());
				globalLocation = globalLocation!=null?globalLocation:spawnLoc.clone();
			}
			spawnLoc = Utils.chooseRandomLocation(globalLocation, radius);
			if(spawnLoc == null){
				Main.getInstance().logInfo(Utils.color("&cThe chest " + getName() + " didn't found a good location, so that it couldn't respawn! " ));
				Utils.scheduleReSpawn(this);
				return false;
			}
			// whatever happens after, the chest will spawn, so we can set this
			despawn();
			setRandomLoc(spawnLoc.clone());
		}
		// if the chest is already spawned, we despawn it
		despawn();

		// handle natural spawning messages - command respawn messages are handled in command class
		if(!forceSpawn && getRespawn_natural() ) {
			String natural_msg = Utils.color((((Main.configs.NOTE_natural_msg.replace("[Chest]", holo)).replace("[x]", spawnLoc.getX()+"")).replace("[y]", spawnLoc.getY()+"")).replace("[z]", spawnLoc.getZ()+"").replace("[World]", world));
			if(Main.configs.NOTE_bungee_broadcast) {
				BungeeChannel.bungeeBroadcast(natural_msg);
			}
			else if(!Main.configs.NOTE_per_world_message) {
				for(World w : Bukkit.getWorlds()) {
					for(Player p : w.getPlayers()) {
						Utils.sendMultilineMessage(natural_msg, p);
					}
				}
			}else {
				for(Player p : spawnLoc.getWorld().getPlayers()){
					Utils.sendMultilineMessage(natural_msg, p);
				}
			}
		}

		// make the fall effect
		final Block newBlock = spawnLoc.getBlock();
		if(getFall()) {
			int height = Main.configs.FALL_Height;
			Location startLocation = new Location(spawnLoc.getWorld(), spawnLoc.getX()+0.5, spawnLoc.getY()+height, spawnLoc.getZ()+0.5);
			new FallingPackageEntity(startLocation, chunk_was_loaded, spawnLoc);
		}
		createchest(newBlock, spawnLoc);
		
		return true;
	}

	public Location getParticleLocation() {
		final Location loc2 = getActualLocation().clone();
		loc2.add(0.5,0.5,0.5);
		return loc2;
	}
	

	/**
	 * @return a clone of the global location
	 */
	public Location getPosition() {			return globalLoc.clone();	}
	/**
	 * @return a clone of the random location
	 */
	public Location getRandomPosition() {	return (randomLoc!=null)?randomLoc.clone():getPosition();	}

	/**
	 * @return a clone of the actual location, eg the random location if the radius is not 0, else the global location
	 */
	public Location getActualLocation() {
		return (radius!=0)?getRandomPosition():getPosition();
	}
	
	/**
	 * @param index the index to set the chance in inventory
	 * @param v the value of the chance to set for "index" item
	 */
	public void setChance(int index, int v) {			chances[index] = v;		}


	/**
	 * @param inventory
	 */
	public void setInventory(Inventory inventory) {
		// if we don't clear the existing inv, removed items will still be in the chest
		inv.clear();
		for(int i = 0 ; i < inventory.getSize() ; i++) {
			if(inventory.getItem(i) != null) {
				inv.setItem( i, inventory.getItem(i));
				if(chances[i] ==0) {
					chances[i] =  Main.configs.default_item_chance;
				}
			}
		}
	}

	/**
	 * Change a chest's location
	 * @param loc the new location
	 */
	public void changepos(Location loc) {
		despawn();
		setWorld(loc.getWorld().getName());
		setGlobalLoc(loc);
		spawn(true);
	}

	/**
	 * Saves the chest in data file, in case of crash, after a modification, or before server shutdown
	 */
	public void updateData() {
		saveInConfig();
		Main.getInstance().getConfigFiles().saveData();
	}
	
	/**
	 * Deletes the chest from data file and despawns it
	 */
	public void deleteChest() {
		despawn();
		Main.getInstance().getLootChest().remove(getName());
		Main.getInstance().getConfigFiles().getData().set("chests."+ getName(), null);
		Main.getInstance().getConfigFiles().saveData();
	}


	/**
	 * @param block
	 * @return true if the block is the good one for the chest
	 */
	public boolean isGoodType(Block block) {
		return type.equals(block.getType());
	}

	/**
	 * gives the main informations about the chest
	 */
	public String toString() {
		return (name +" "+fall +" " +direction+" "+ radius+" "+particle);             
	}
	
	/**
	 * sets the text of the lootchest's holo
	 * @param text the text to set
	 */
	public void setHolo(String text) {
		holo = text;
		hologram.remove();
		hologram.setText(holo);
	}

	public void setLastReset() {
		this.lastReset = (new Timestamp(System.currentTimeMillis())).getTime();
	}

	/**
	 * reactivates particles, after a server restart for example
	 * @param lc
	 */
	public void reactivateEffects() {

		Location loc = getActualLocation();
		//if the lootchest isn't here, let's not spawn particles or anything
		if(!isGoodType(loc.getBlock())) {
			return;
		}
		getHologram().setLoc(loc);
		if(Main.getVersion()>7 && getFall() && Main.configs.FALL_Let_Block_Above_Chest_After_Fall){
			Location arm = loc.clone();
			arm.add(0.5, 2, 0.5);
			Material mat = Material.valueOf(Main.configs.FALL_Block);
			Entity ent = loc.getWorld().spawnEntity(arm, org.bukkit.entity.EntityType.ARMOR_STAND);
			
			
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
		final Location loc2 = getParticleLocation();
		for(Particle part : Main.getInstance().getSupportedParticles()) {
			if(getParticle() != null && (""+part).contains(getParticle().name())) {
				Main.getInstance().getPart().put(loc2, part);
			}
		}
		
	}
    
}