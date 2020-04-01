package fr.black_eyes.lootchest;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.black_eyes.lootchest.commands.LootchestCommand;
import fr.black_eyes.lootchest.listeners.DeleteListener;
import fr.black_eyes.lootchest.listeners.InventoryListeners;

import fr.black_eyes.lootchest.Utils;







public class Main extends JavaPlugin {
	
	public static Object particules[] = new Object[34];
	public static HashMap<Location, Object> part = new HashMap<Location, Object>();
	private static Main instance;
	private static Config config;
	private static Utils utils;
	
	public void onDisable() {
		
		config.saveData();	
	}
	
    
	
	public void onEnable() {
		instance = this;
		config = new Config();
		utils = new Utils();
		
		
		if(!config.initFiles()) {
        	getLogger().info("§cThe data file couldn't be initialised, the plugin will stop.");
        	return;
        }
		
		this.getServer().getPluginManager().registerEvents(new Utils(), this);
		this.getServer().getPluginManager().registerEvents(new DeleteListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        this.getCommand("lootchest").setExecutor(new LootchestCommand());
        this.getCommand("lootchest").setTabCompleter(new LootchestCommand());
        super.onEnable();
        
        //In many versions, I add some text an config option. These lines are done to update config and language files without erasing options that are already set
        config.setConfig("CheckForUpdates", true);
        config.setConfig("Particles.enable", true);
        config.setConfig("Hologram_distance_to_chest", 1);
        config.setConfig("UseHologram", true);
        config.setConfig("RemoveEmptyChests", true);
        config.setConfig("RemoveChestAfterFirstOpenning", false);
        config.setConfig("respawn_notify.natural_respawn.enabled", true);
        config.setConfig("respawn_notify.respawn_with_command.enabled", true);
        config.setConfig("respawn_notify.respawn_all_with_command.enabled", true);
        config.setConfig("respawn_notify.natural_respawn.message", "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!");
        config.setConfig("respawn_notify.respawn_with_command.message", "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!");
        config.setConfig("respawn_notify.respawn_all_with_command.message", "&6All chests where forced to respawn! Get them guys!");
        config.setConfig("PreventHopperPlacingUnderLootChest", true);
        config.setConfig("check_for_respawn_in_ticks", 600);
        config.setConfig("respawn_notify.respawn_all_in_one_check.enabled", true);
        config.setConfig("respawn_notify.respawn_all_in_one_check.message", "&6All loot chests respawned");
        config.setConfig("Enable_fall_effect", true);
        config.setConfig("check_for_respawn_in_ticks", null);
        config.setConfig("Fall_Effect_Height", 50);
        config.setConfig("respawn_notify.per_world_message", true);
        config.setConfig("respawn_notify.message_on_chest_take", true);
        config.setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");
        config.setLang("PlayerIsNotOnline", "&cThe player [Player] is not online");
        config.setLang("givefrom", "&aYou were given the [Chest] chest by [Player]");
        config.setLang("giveto", "&aYou gave the chest [Chest] to player [Player]");
        config.setLang("ListCommand", "&aList of all chests: [List]");
        config.setLang("help.line10", "&a/lc reload &b: reloads the plugin");
        config.setLang("help.line11", "&a/lc list &b: list all chests");
        config.setLang("help.line13", "&a/lc give <player> <name> &b: gives the chest <name> to player <player>");
        config.setLang("help.line14", "&a/lc settime <name> &b: sets the respawn time of a chest in seconds");
        config.setLang("help.line15", "&a/lc randomspawn <name> <radius> &b: make a chest respawn randomly in the specified radius");
        config.setLang("Menu.main.copychest", "&1Copy settings from anyther chest");
        config.setLang("Menu.copy.name", "&1Choose a chest to copy its settings");
        config.setLang("copiedChest", "&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]");
        config.setLang("changedPosition", "&6You set the location of chest &b[Chest] &6to your location");
        config.setLang("help.line12", "&a/lc setpos &b: edit the position of a chest");
        config.setLang("settime", "&6You successfully set the time of the chest &b[Chest]");
        config.setLang("Menu.time.infinite", "&6Desactivates the respawn time");
        config.setLang("chestRadiusSet", "&aYou defined a spawn radius for the chest [Chest]");
        config.setLang("Menu.copy.page", "&2---> Page &b[Number]");
        config.setLang("teleportedToChest", "&aYou were teleported to chest [Chest]");
        config.setLang("help.line16", "&a/lc tp <name> &b: teleports you to a chest");
        config.setLang("help.line17","&a/lc togglefall <name> &b: enable/disable the fall effect for a chest");
        config.setLang("enabledFallEffect", "&aYou enabled fall effect for chest &b[Chest]");
        config.setLang("disabledFallEffect", "&cYou disabled fall effect for chest &b[Chest]");
        config.setLang("playerTookChest", "&6Oh no! &b[Player] &6found the chest &b[Chest] &6and took everything in it!");
        config.setLang("disabledChestRadius", "&cYou disabled random spawn for chest [Chest]");
        if(config.getConfig().getBoolean("CheckForUpdates")) {
        	Updater.checkversion();
        }
        //initialisation des matériaux dans toutes les verions du jeu
        //initializing materials in all game versions, to allow cross-version compatibility
        Mat.init_materials();
        
        //1.8 version uses a totally different particle system. It would need many more time to make it working.
        if(!Utils.checkDeprec()) {
    		initParticles();
        }
        if(Utils.checkDeprec()) {
        	config.getConfig().set("Particles.enable", false);
        	getLogger().info("Spigot 1.8 detected: particles were disabled");
        }
        
        //One particle was created in 1.13 so that other versions won't have it. Let's remove it if you're not in 1.13
        else if (!Bukkit.getVersion().contains("1.13") && !Bukkit.getVersion().contains("1.14") && !Bukkit.getVersion().contains("1.15")) {
        	particules[21] = org.bukkit.Particle.valueOf("FOOTSTEP");
        }
        else if(Bukkit.getVersion().contains("1.14")) {
        	
        }


        //Initialisation des particules
        //Particle initialization
        if(!Utils.checkDeprec()) {    
        	//loop de tous les coffres tous les 1/4 (modifiable dans la config) de secondes pour faire spawn des particules
        	//loop of all chests every 1/4 (editable in config) of seconds to spawn particles 
        	new BukkitRunnable() {
        		public void run() {
        			double radius = config.getConfig().getDouble("Particles.radius");
        			if (config.getConfig().getBoolean("Particles.enable")) {
        				for(Location keys : part.keySet()) {
        					
        					if((org.bukkit.Particle) part.get(keys) != org.bukkit.Particle.REDSTONE) {
        						keys.getWorld().spawnParticle( (org.bukkit.Particle) part.get(keys), keys, config.getConfig().getInt("Particles.number"), radius, radius, radius, config.getConfig().getDouble("Particles.speed"));
        					} 
        					
        				}
        			}
        		}
        	}.runTaskTimer(this, 0, getConfig().getInt("Particles.respawn_ticks"));
        }

        
    	boolean allchestrespawns = true;
    	int numchest = 0;	
    	for(String keys : config.getData().getConfigurationSection("chests").getKeys(false)) {
    		numchest++;
    		if(!config.getData().isSet("chests." + keys + ".time") ) {
    			config.getData().set("chests." + keys, null);
				config.reloadData();
    		}
    		else if(Bukkit.getWorld(config.getData().getString("chests." + keys + ".position.world")) != null) {
    			//if the chest didn't respawn at startup, we start its timer
    			if (!utils.restoreChest(keys, false)) {
    				allchestrespawns=false;
    				utils.sheduleRespawn(keys);
    			}
    			utils.reactivateEffects(keys);
    		}
    		else {
    			getLogger().info("§cCouldn't load chest "+keys +" : the world " + config.getData().getString("chests." + keys + ".position.world") + " is not loaded");
    		}
    	}
    	if(numchest >1 && allchestrespawns && config.getConfig().getBoolean("respawn_notify.respawn_all_in_one_check.enabled")) 
    		Bukkit.broadcastMessage(config.getConfig().getString("respawn_notify.respawn_all_in_one_check.message").replace("&", "§"));

            
    }
	
	public static Main getInstance() {
        return instance;
    }
	public static Config getConfigFiles() {
        return config;
    }
	
	public FileConfiguration getData() {
		return config.getData();
		
	}
	
	
	
	

	
	//particle initialozation
	private void initParticles() {
		org.bukkit.Particle parti[] = {org.bukkit.Particle.EXPLOSION_HUGE, org.bukkit.Particle.EXPLOSION_LARGE, org.bukkit.Particle.EXPLOSION_NORMAL, org.bukkit.Particle.FIREWORKS_SPARK, org.bukkit.Particle.WATER_BUBBLE, org.bukkit.Particle.SUSPENDED, org.bukkit.Particle.TOWN_AURA, org.bukkit.Particle.CRIT, org.bukkit.Particle.CRIT_MAGIC, org.bukkit.Particle.SMOKE_NORMAL, org.bukkit.Particle.SMOKE_LARGE, org.bukkit.Particle.SPELL_MOB, org.bukkit.Particle.SPELL_MOB_AMBIENT, org.bukkit.Particle.SPELL, org.bukkit.Particle.SPELL_INSTANT, org.bukkit.Particle.SPELL_WITCH, org.bukkit.Particle.NOTE, org.bukkit.Particle.PORTAL, org.bukkit.Particle.ENCHANTMENT_TABLE, org.bukkit.Particle.FLAME, org.bukkit.Particle.LAVA, org.bukkit.Particle.LAVA, org.bukkit.Particle.WATER_SPLASH, org.bukkit.Particle.WATER_WAKE, org.bukkit.Particle.CLOUD, org.bukkit.Particle.SNOWBALL, org.bukkit.Particle.DRIP_WATER, org.bukkit.Particle.DRIP_LAVA, org.bukkit.Particle.SNOW_SHOVEL, org.bukkit.Particle.SLIME, org.bukkit.Particle.HEART, org.bukkit.Particle.VILLAGER_ANGRY, org.bukkit.Particle.VILLAGER_HAPPY, org.bukkit.Particle.BARRIER};
		for(int i = 0; i<parti.length; i++) {
			particules[i] = parti[i];
		}
	}
}
