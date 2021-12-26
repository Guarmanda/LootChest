package fr.black_eyes.lootchest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import fr.black_eyes.lootchest.commands.LootchestCommand;
import fr.black_eyes.lootchest.listeners.Armorstand;
import fr.black_eyes.lootchest.listeners.DeleteListener;
import fr.black_eyes.lootchest.listeners.InventoryListeners;
import fr.black_eyes.lootchest.particles.Particle;
import lombok.Getter;







public class Main extends JavaPlugin {
	//public ArrayList<LootChest> lc = new ArrayList<LootChest>();
	@Getter private Particle particules[];
	@Getter private HashMap<String, Particle> particles = new HashMap<String, Particle>();
	@Getter private HashMap<Location, Particle> part = new HashMap<Location, Particle>();
	public static Config configs;
	public static int players;
	@Getter private HashMap<String, Lootchest> LootChest;
	@Getter private static Main instance;
	@Getter private Files configFiles;
	@Getter private Utils utils;
	@Getter private Boolean UseArmorStands;
	@Getter private Menu menu;
	private static int version;
	
	//the way holograms are working changed a lot since 2.2.4. 
	//If user just done the update, this will be auto set to true by detecting a lacking config option
	//that appeared precisely in 2.2.4
	@Getter private boolean killOldHolograms = false;
	

	public void onDisable() {
		if(getVersion()!=7) {
			for(Lootchest lc : LootChest.values()) {
				lc.getHologram().remove();
			}
		}
		utils.updateData();
		backUp();
		logInfo("&aBacked up data file in case of crash");
	}
	
	/**
	 * Send a message to logs with colours, only if logs are enabled in config.
	 * @param msg the message to send
	 */
    public void logInfo(String msg) {
    	if(configFiles.getConfig() ==null || !configFiles.getConfig().isSet("ConsoleMessages") || configFiles.getConfig().getBoolean("ConsoleMessages")) {
    		instance.getLogger().info(msg.replace("&", "§"));
    	}
    }
    
    /**
     * Check if bungee is enabled in spigot config
     * @return true if bungee is enabed, else false
     */
    private boolean hasBungee(){
        boolean bungee = SpigotConfig.bungee;
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        if(bungee && (!(onlineMode))){
            return true;
        }
        return false;
    }
	
	/**
	 * Returns the version of your server (1.x)
	 * 
	 * @return The version number
	 */
	public static int getVersion() {
		if(version == 0) {
			version = Integer.parseInt((Bukkit.getBukkitVersion().split("-")[0]).split("[.]")[1]);
		}
		return version;
	}
    
	public void onEnable() {
		instance = this;
		configFiles = new Files();
		LootChest = new HashMap<String, Lootchest>();
		utils = new Utils();
		menu = new Menu();
		UseArmorStands = true;
		//initialisation des matÃ©riaux dans toutes les verions du jeu
        //initializing materials in all game versions, to allow cross-version compatibility
        Mat.init_materials();
		logInfo("Server version: 1." + getVersion() );
		logInfo("Loading config files...");
		if(!configFiles.initFiles()) {
        	getLogger().info("§cConfig or data files couldn't be initialized, the plugin will stop.");
        	return;
        }
		if(getVersion() !=7){
			this.getServer().getPluginManager().registerEvents(new Armorstand(), this);
		}
		this.getServer().getPluginManager().registerEvents(new DeleteListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
		LootchestCommand cmd =  new LootchestCommand();
        this.getCommand("lootchest").setExecutor(cmd);
        this.getCommand("lootchest").setTabCompleter(cmd);
        super.onEnable();
        
        
        //In many versions, I add some text an config option. These lines are done to update config and language files without erasing options that are already set
        updateOldConfig();
        configFiles.saveConfig();
        configFiles.saveLang();
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "BungeeCord", (org.bukkit.plugin.messaging.PluginMessageListener)new BungeeChannel());
        
        //If we're on paperspigot, in 1.9+, we check if armorstands ticks aren't disabled. If they are, we use 
        //falling blocks insteand of armor stands for fall effect
        if(Bukkit.getVersion().contains("Paper") && getVersion()!=8 && getVersion()!=7 ) {
        	if(org.bukkit.Bukkit.getServer().spigot().getPaperConfig().isSet("world-settings.default.armor-stands-tick")) {
	        	if(!org.bukkit.Bukkit.getServer().spigot().getPaperConfig().getBoolean("world-settings.default.armor-stands-tick")) {
	        		UseArmorStands = false;
	        		getLogger().info("§eYou disabled 'armor-stands-tick' in paper.yml. ArmorStands will not have gravity, so fall effect will use falling blocks instead! Some blocks can't be used as falling blocks. If so, only fireworks will show!");
	        		getLogger().info("§eIf no blocks are spawned with the fireworks, use another type of block for fall-effect in config.yml or enable 'armor-stands-tick' in paper.yml");
	        	}
        	}
        }
        
        //If we enabled bungee broadcast but we aren't on a bungee server, not any message will show
        configs= Config.getInstance(configFiles.getConfig());
        if(!hasBungee() && configs.NOTE_bungee_broadcast) {
    		getLogger().info("§cYou enaled bungee broadcast in config but you didn't enable bungeecord in spigot config!");
    		getLogger().info("§cSo if this server isn't in a bungee network, no messages will be sent at all on chest spawn!");
        }
 

        

        if(configs.CheckForUpdates) {
        	logInfo("Checking for update...");
        	 new Updater(this);
        }

        
        
        logInfo("Starting particles...");
        
        //Initialization of particles values, it doesn't spawn them but is used in spawning
    	initParticles();
  
    	//loop de tous les coffres tous les 1/4 (modifiable dans la config) de secondes pour faire spawn des particules
    	//loop of all chests every 1/4 (editable in config) of seconds to spawn particles 
    	startParticles();
    	
    	//Loads all chests asynchronously
    	loadChests();
        
	}
	
	
	/**
	 * Loop all chests every 1/4 of second (configurable in config.yml) and spawns particles around it.
	 * Servers with bad performances (or with 400 chests) should disable particles.
	 */
	private void startParticles() {
		new BukkitRunnable() {
    		public void run() {
    			float radius = (float) configs.PART_radius;
    			float speed = (float)configs.PART_speed;
    			int number = configs.PART_number;
    			if (configs.PART_enable) {
    				for(Location keys : part.keySet()) {
    					Boolean loaded = keys.getWorld().isChunkLoaded((int)keys.getX()/16, (int)keys.getZ()/16) ;
    					if (loaded) {
    						new Thread(() -> {
		    					part.get(keys).display(radius, radius, radius, speed, number, keys, keys.getWorld().getPlayers());
    						}).start();
    					}
    				}
    			}
    		}
    	}.runTaskTimer(this, 0, configs.PART_respawn_ticks);
	}
    		
	/**
	 * Loads all chests asynchronously
	 */
	private void loadChests() {
		Integer cooldown = configs.Cooldown_Before_Plugin_Start;
    	if(cooldown>0) {
    		logInfo("Chests will load in "+ cooldown + " seconds.");
    	}
    	
        this.getServer().getScheduler().runTaskLater(this, (Runnable)new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
		    	logInfo("Loading chests...");
		    	long current = (new Timestamp(System.currentTimeMillis())).getTime();
				for(String keys : configFiles.getData().getConfigurationSection("chests").getKeys(false)) {
					String name = configFiles.getData().getString("chests." + keys + ".position.world");
					String randomname = name;
					if( configFiles.getData().getInt("chests." + keys + ".randomradius")>0) {
						 randomname = configFiles.getData().getString("chests." + keys + ".randomPosition.world");
					}
					if(name != null && org.bukkit.Bukkit.getWorld(randomname) != null && org.bukkit.Bukkit.getWorld(name) != null) {
						getLootChest().put(keys, new Lootchest(keys));
					}
					else {
		    			getLogger().info("§cCouldn't load chest "+keys +" : the world " + configFiles.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
					}
		    	}
				if(getVersion()!=7) {
					utils.killOldHolograms(true);
				}
				
				logInfo("Loaded "+LootChest.size() + " Lootchests in "+((new Timestamp(System.currentTimeMillis())).getTime()-current) + " miliseconds");
				logInfo("Starting LootChest timers asynchronously...");
				for (final Lootchest lc : LootChest.values()) {
		            Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, () -> {
		                    Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
		                    		Boolean loaded = Bukkit.getWorld(lc.getWorld()).isChunkLoaded((int)lc.getActualLocation().getX()/16, (int)lc.getActualLocation().getZ()/16) ;
		                            if (!utils.restoreChest(lc, false)) {
		                                utils.sheduleRespawn(lc);
		                            }
		                            Boolean loaded2 = Bukkit.getWorld(lc.getWorld()).isChunkLoaded((int)lc.getActualLocation().getX()/16, (int)lc.getActualLocation().getZ()/16) ;
		                    		if(loaded != loaded2) {
		                    			//logInfo("Chunk loaded during spawn: "+lc.toString());
		                    		}
		                            utils.reactivateEffects(lc);
		                            Boolean loaded3 = Bukkit.getWorld(lc.getWorld()).isChunkLoaded((int)lc.getActualLocation().getX()/16, (int)lc.getActualLocation().getZ()/16) ;
		                    		if(loaded3 != loaded2) {
		                    			//logInfo("Chunk loaded during particles: "+lc.toString());
		                    		}
		                    }, 0L);
		            }, 5L);
		        }
		    	logInfo("Plugin loaded");
            
	        }
	    }, cooldown*20);
	}
	
	
  /**
   * In many versions, I add some text an config option. 
   * These lines are done to update config and language files without erasing options that are already set
   */
  private void updateOldConfig() {
	  configFiles.setConfig("Particles.enable", true);
      configFiles.setConfig("Hologram_distance_to_chest", 1);
      configFiles.setConfig("UseHologram", true);
      configFiles.setConfig("RemoveEmptyChests", true);
      configFiles.setConfig("RemoveChestAfterFirstOpenning", false);
      configFiles.setConfig("respawn_notify.natural_respawn.enabled", true);
      configFiles.setConfig("respawn_notify.respawn_with_command.enabled", true);
      configFiles.setConfig("respawn_notify.respawn_all_with_command.enabled", true);
      configFiles.setConfig("respawn_notify.natural_respawn.message", "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!");
      configFiles.setConfig("respawn_notify.respawn_with_command.message", "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!");
      configFiles.setConfig("respawn_notify.respawn_all_with_command.message", "&6All chests where forced to respawn! Get them guys!");
      configFiles.setConfig("PreventHopperPlacingUnderLootChest", true);
      configFiles.setConfig("respawn_notify.per_world_message", true);
      configFiles.setConfig("respawn_notify.message_on_chest_take", true);
      configFiles.setConfig("Minimum_Number_Of_Players_For_Natural_Spawning", 0);
      configFiles.setConfig("use_players_locations_for_randomspawn", false);
      configFiles.setConfig("Cooldown_Before_Plugin_Start", 0);
      configFiles.setConfig("Prevent_Chest_Spawn_In_Protected_Places", false);
      configFiles.setConfig("WorldBorder_Check_For_Spawn", true);
      configFiles.setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");
      configFiles.setLang("PlayerIsNotOnline", "&cThe player [Player] is not online");
      configFiles.setLang("givefrom", "&aYou were given the [Chest] chest by [Player]");
      configFiles.setLang("giveto", "&aYou gave the chest [Chest] to player [Player]");
      configFiles.setLang("ListCommand", "&aList of all chests: [List]");
      configFiles.setLang("Menu.main.copychest", "&1Copy settings from anyther chest");
      configFiles.setLang("Menu.copy.name", "&1Choose a chest to copy its settings");
      configFiles.setLang("copiedChest", "&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]");
      configFiles.setLang("changedPosition", "&6You set the location of chest &b[Chest] &6to your location");
      configFiles.setLang("settime", "&6You successfully set the time of the chest &b[Chest]");
      configFiles.setLang("Menu.time.infinite", "&6Desactivates the respawn time");
      configFiles.setLang("chestRadiusSet", "&aYou defined a spawn radius for the chest [Chest]");
      configFiles.setLang("Menu.copy.page", "&2---> Page &b[Number]");
      configFiles.setLang("Menu.particles.page", "&2---> Page &b[Number]");
      configFiles.setLang("teleportedToChest", "&aYou were teleported to chest [Chest]");
      configFiles.setLang("enabledFallEffect", "&aYou enabled fall effect for chest &b[Chest]");
      configFiles.setLang("disabledFallEffect", "&cYou disabled fall effect for chest &b[Chest]");
      configFiles.setLang("playerTookChest", "&6Oh no! &b[Player] &6found the chest &b[Chest] &6and took everything in it!");
      configFiles.setLang("disabledChestRadius", "&cYou disabled random spawn for chest [Chest]");
      configFiles.setLang("Menu.main.disable_fall", "&aFall effect is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_respawn_natural", "&aNatural-respawn message is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_respawn_cmd", "&aCommand-respawn message is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_take_message", "&aMessage on chest take is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.enable_fall", "&cFall effect is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.enable_respawn_natural", "&cNatural-respawn message is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.enable_respawn_cmd", "&cCommand-respawn message is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.type", "&1Choose type (Barrel, trapped chest, chest)");
      configFiles.setLang("Menu.type.name", "&1Choose type (Barrel, trapped chest, chest)");
      configFiles.setLang("Menu.main.enable_take_message", "&cMessage on chest take is disabled. Click to &aENABLE &cit");
      configFiles.setLang("locate_command.main_message",  "&6Location of loot chests:");
      configFiles.setLang("editedChestType", "&aEdited type of chest &b[Chest]");
      configFiles.setLang("locate_command.chest_list", "- &b[Chest]&6: [x], [y], [z] in world [world]");
      configFiles.setLang("removedHolograms", "&aSuccessfully removed &b[Number] LootChest holograms.");
      configFiles.setLang("CantOpenLootchestBecauseMonster", "&cYou can't open this chest while there is [Number] monsters nearby");
      if (configFiles.getLang().isSet("help.line1")) {
          final List<String> tab = new ArrayList<String>();
          for (int i = 1; i <= 17; ++i) {
              if (configFiles.getLang().getString("help.line" + i) != null) {
                  tab.add(configFiles.getLang().getString("help.line" + i));
              }
          }
          configFiles.getLang().set("help", (Object)tab);
          try {
              configFiles.getLang().save(configFiles.getLangF());
              configFiles.getLang().load(configFiles.getLangF());
          }
          catch (IOException | InvalidConfigurationException e) {
              e.printStackTrace();
          }
      }
      configFiles.setConfig("Fall_Effect.Let_Block_Above_Chest_After_Fall", false);
      configFiles.setConfig("Fall_Effect.Optionnal_Color_If_Block_Is_Wool", "CYAN");
      configFiles.setConfig("Fall_Effect.Block",  configFiles.getConfig().getString("Fall_Effect_Block"));
      configFiles.setConfig("Fall_Effect.Height",  configFiles.getConfig().getInt("Fall_Effect_Height"));
      configFiles.setConfig("Fall_Effect.Enabled",  configFiles.getConfig().getBoolean("Enable_fall_effect"));
      configFiles.setConfig("Fall_Effect.Enable_Fireworks",  true);
      configFiles.setConfig("Fall_Effect.Speed", 0.9);
      configFiles.setConfig("respawn_notify.bungee_broadcast", false);
      configFiles.setConfig("ConsoleMessages", true);
      configFiles.setConfig("save_Chest_Locations_At_Every_Spawn", true);
      configFiles.setConfig("Protect_From_Explosions", false);
      configFiles.setConfig("Radius_Without_Monsters_For_Opening_Chest", 0);
      if(!configFiles.getConfig().isSet("Destroy_Naturally_Instead_Of_Removing_Chest")) {
    	  killOldHolograms = true;
      }
      configFiles.setConfig("Destroy_Naturally_Instead_Of_Removing_Chest", true);
      configFiles.setLang("Menu.time.notInfinite", "&6Reactivate respawn time");
      configFiles.setLang("commandGetName", "&6Your'e looking the chest &b[Chest]");
      if(!configFiles.getLang().getStringList("help").toString().contains("getname")){
      	Bukkit.broadcastMessage(configFiles.getLang().getStringList("help").toString());
      	List<String> help = configFiles.getLang().getStringList("help");
      	help.add("&a/lc getname &b: get the name of the targeted LootChest");
      	configFiles.getLang().set("help", help);
      	configFiles.saveLang();
      }
      if(!configFiles.getLang().getStringList("help").toString().contains("locate")){
      	Bukkit.broadcastMessage(configFiles.getLang().getStringList("help").toString());
      	List<String> help = configFiles.getLang().getStringList("help");
      	help.add("&a/lc locate &b: gives locations of all chests that haves natural respawn message enabled");
      	configFiles.getLang().set("help", help);
      	configFiles.saveLang();        	
      }
      if(!configFiles.getLang().getStringList("help").toString().contains("removeAllHolo")){
    	Bukkit.broadcastMessage(configFiles.getLang().getStringList("help").toString());
    	List<String> help = configFiles.getLang().getStringList("help");
    	help.add("&a/lc removeAllHolo &b: removes only bugged LootChest holograms without chest under them");
    	configFiles.getLang().set("help", help);
    	configFiles.saveLang();        	
      }
    	
      if(configFiles.getConfig().isSet("Optionnal_Color_If_ArmorStand_Head_Is_Wool")) {
      	configFiles.getConfig().set("Fall_Effect.Optionnal_Color_If_Block_Is_Wool",configFiles.getConfig().getString("Optionnal_Color_If_ArmorStand_Head_Is_Wool") );
      	configFiles.getConfig().set("Optionnal_Color_If_ArmorStand_Head_Is_Wool", null);
      	configFiles.getConfig().set("Fall_Effect.Block", configFiles.getConfig().getString("Armor_Stand_Head_Item"));
      	configFiles.getConfig().set("Armor_Stand_Head_Item", null);
      	configFiles.getConfig().set("Use_ArmorStand_Instead_Of_Block", null);
      	configFiles.getConfig().set("Fall_Effect.Let_Block_Above_Chest_After_Fall", configFiles.getConfig().getBoolean("Let_ArmorStand_On_Chest_After_Fall"));
      	configFiles.getConfig().set("Let_ArmorStand_On_Chest_After_Fall", null);
      	configFiles.saveConfig();
      }
      if(configFiles.getConfig().isSet("Fall_Effect_Height")){
      	configFiles.getConfig().set("Fall_Effect_Height", null);
      	configFiles.getConfig().set("Fall_Effect_Block", null);
      	configFiles.getConfig().set("Enable_fall_effect", null);
      	configFiles.saveConfig();
      }
      if(!configFiles.getConfig().isSet("Timer_on_hologram.Show_Timer_On_Hologram")) {
    	  boolean timeholo = configFiles.getConfig().getBoolean("Show_Timer_On_Hologram");
    	  configFiles.getConfig().set("Show_Timer_On_Hologram", null);
    	  configFiles.getConfig().set("Timer_on_hologram.Show_Timer_On_Hologram", timeholo);
    	  configFiles.getConfig().set("Timer_on_hologram.Hours_Separator", " hours, ");
    	  configFiles.getConfig().set("Timer_on_hologram.Minutes_Separator", " minutes  and ");
    	  configFiles.getConfig().set("Timer_on_hologram.Seconds_Separator", " seconds.");
    	  configFiles.getConfig().set("Timer_on_hologram.Format", "&3%Hours%Hsep%Minutes%Msep%Seconds%Ssep &bleft for %Hologram to respawn");
      }
      if(configFiles.getLang().getString("Menu.chances.lore").equals("&aLeft click: +1; right: -1; shift+right: -10; shift+left: +10; tab+right: -50") || configFiles.getLang().getString("Menu.chances.lore").equals("&aLeft click to up percentage, Right click to down it")) {
      	configFiles.getLang().set("Menu.chances.lore", "&aLeft click: +1||&aright: -1||&ashift+right: -10||&ashift+left: +10||&atab+right: -50");
      	configFiles.saveLang();
      }
      
  }
	

	
	/**
	 * This initialize an array of particles. Under 1.12, I use InventiveTalent's ParticleAPI, 
	 * and for 1.12+, I use new particles spawning functions so I use default spigot particles
	 */
	private void initParticles() {
		int cpt = 0;
		for(Particle p:Particle.values()) {
			if(p.isSupported()) {
				cpt++;
			}
		}
		particules = new Particle[cpt];
		int i = 0;
		for(Particle p:Particle.values()) {
			if(p.isSupported()) {
				particles.put(p.getName(), p);
				particules[i++] = p;
			}
		}
	}
	
	/**
	 * Creates a backup of data.yml, which is sometimes lost by plugin users in some rare cases.
	 */
	public void backUp() {
		File directoryPath = new File(instance.getDataFolder() + "/backups/");
		if(!directoryPath.exists()) {
			directoryPath.mkdir();
		}
		List<String> contents = Arrays.asList(directoryPath.list());
		int i=0;
		//finding valid backup name
		if(!contents.isEmpty()) {
			while( !contents.contains(i+"data.yml")) i++;
		}
		while( contents.contains(i+"data.yml")) {
			if (contents.contains((i+10)+"data.yml")) {
				Path oldbackup = Paths.get(instance.getDataFolder() +"/backups/"+ (i)+"data.yml");
				try {
					java.nio.file.Files.deleteIfExists(oldbackup);
				} catch (IOException e) {
					e.printStackTrace();
				}
				i+=9;
			}
			i++;
		}
		
		//auto-deletion of backup to keep only the 10 last ones
		Path oldbackup = Paths.get(instance.getDataFolder() +"/backups/"+ (i-10)+"data.yml");
		try {
			java.nio.file.Files.deleteIfExists(oldbackup);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//backing up
		Path source = Paths.get(instance.getDataFolder() + "/data.yml");
	    Path target = Paths.get(instance.getDataFolder() + "/backups/"+i+"data.yml");
	    try {
	    	java.nio.file.Files.copy(source, target);
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	}


}
