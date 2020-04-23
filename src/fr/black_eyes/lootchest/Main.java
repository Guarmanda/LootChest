package fr.black_eyes.lootchest;

import static org.inventivetalent.reflection.minecraft.Minecraft.Version.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.particle.ParticleEffect;
import org.inventivetalent.reflection.minecraft.Minecraft;

import fr.black_eyes.lootchest.commands.LootchestCommand;
import fr.black_eyes.lootchest.listeners.Armorstand;
import fr.black_eyes.lootchest.listeners.DeleteListener;
import fr.black_eyes.lootchest.listeners.InventoryListeners;

import fr.black_eyes.lootchest.Utils;







public class Main extends JavaPlugin {
	//public ArrayList<LootChest> lc = new ArrayList<LootChest>();
	public static Object particules[] = new Object[34];
	public static HashMap<Location, Object> part = new HashMap<Location, Object>();
	public static HashMap<String, Lootchest> LootChest = new HashMap<String, Lootchest>();
	private static Main instance;
	private static Config config;
	private static Utils utils;
	
	public void onDisable() {
		utils.updateData();
	}
	
    public static void logInfo(String msg) {
    	if(config.getConfig() ==null || !config.getConfig().isSet("ConsoleMessages") || config.getConfig().getBoolean("ConsoleMessages")) {
    		instance.getLogger().info(msg.replace("&", "§"));
    	}
    }
	
	@SuppressWarnings("deprecation")
	public void onEnable() {
		instance = this;
		config = new Config();
		utils = new Utils();
		
		logInfo("Loading config files...");
		if(!config.initFiles()) {
        	getLogger().info("§cConfig or data files couldn't be initialized, the plugin will stop.");
        	return;
        }
		if(!org.bukkit.Bukkit.getVersion().contains("1.7")){
			this.getServer().getPluginManager().registerEvents(new Armorstand(), this);
		}
		this.getServer().getPluginManager().registerEvents(new DeleteListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        this.getCommand("lootchest").setExecutor(new LootchestCommand());
        this.getCommand("lootchest").setTabCompleter(new LootchestCommand());
        super.onEnable();
        
        
        //In many versions, I add some text an config option. These lines are done to update config and language files without erasing options that are already set
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
        config.setConfig("respawn_notify.per_world_message", true);
        config.setConfig("respawn_notify.message_on_chest_take", true);
        config.setConfig("Minimum_Number_Of_Players_For_Natural_Spawning", 0);
        config.setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");
        config.setLang("PlayerIsNotOnline", "&cThe player [Player] is not online");
        config.setLang("givefrom", "&aYou were given the [Chest] chest by [Player]");
        config.setLang("giveto", "&aYou gave the chest [Chest] to player [Player]");
        config.setLang("ListCommand", "&aList of all chests: [List]");
        config.setLang("Menu.main.copychest", "&1Copy settings from anyther chest");
        config.setLang("Menu.copy.name", "&1Choose a chest to copy its settings");
        config.setLang("copiedChest", "&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]");
        config.setLang("changedPosition", "&6You set the location of chest &b[Chest] &6to your location");
        config.setLang("settime", "&6You successfully set the time of the chest &b[Chest]");
        config.setLang("Menu.time.infinite", "&6Desactivates the respawn time");
        config.setLang("chestRadiusSet", "&aYou defined a spawn radius for the chest [Chest]");
        config.setLang("Menu.copy.page", "&2---> Page &b[Number]");
        config.setLang("teleportedToChest", "&aYou were teleported to chest [Chest]");
        config.setLang("enabledFallEffect", "&aYou enabled fall effect for chest &b[Chest]");
        config.setLang("disabledFallEffect", "&cYou disabled fall effect for chest &b[Chest]");
        config.setLang("playerTookChest", "&6Oh no! &b[Player] &6found the chest &b[Chest] &6and took everything in it!");
        config.setLang("disabledChestRadius", "&cYou disabled random spawn for chest [Chest]");
        config.setLang("Menu.main.disable_fall", "&aFall effect is enabled. Click to &cDISABLE &ait");
        config.setLang("Menu.main.disable_respawn_natural", "&aNatural-respawn message is enabled. Click to &cDISABLE &ait");
        config.setLang("Menu.main.disable_respawn_cmd", "&aCommand-respawn message is enabled. Click to &cDISABLE &ait");
        config.setLang("Menu.main.disable_take_message", "&aMessage on chest take is enabled. Click to &cDISABLE &ait");
        config.setLang("Menu.main.enable_fall", "&cFall effect is disabled. Click to &aENABLE &cit");
        config.setLang("Menu.main.enable_respawn_natural", "&cNatural-respawn message is disabled. Click to &aENABLE &cit");
        config.setLang("Menu.main.enable_respawn_cmd", "&cCommand-respawn message is disabled. Click to &aENABLE &cit");
        config.setLang("Menu.main.enable_take_message", "&cMessage on chest take is disabled. Click to &aENABLE &cit");
        if (config.getLang().isSet("help.line1")) {
            final List<String> tab = new ArrayList<String>();
            for (int i = 1; i <= 17; ++i) {
                if (config.getLang().getString("help.line" + i) != null) {
                    tab.add(config.getLang().getString("help.line" + i));
                }
            }
            config.getLang().set("help", (Object)tab);
            try {
                config.getLang().save(config.getLangF());
                config.getLang().load(config.getLangF());
            }
            catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        config.setConfig("Fall_Effect.Let_Block_Above_Chest_After_Fall", false);
        config.setConfig("Fall_Effect.Optionnal_Color_If_Block_Is_Wool", "CYAN");
        config.setConfig("Fall_Effect.Block",  config.getConfig().getString("Fall_Effect_Block"));
        config.setConfig("Fall_Effect.Height",  config.getConfig().getInt("Fall_Effect_Height"));
        config.setConfig("Fall_Effect.Enabled",  config.getConfig().getBoolean("Enable_fall_effect"));
        config.setConfig("Fall_Effect.Enable_Fireworks",  true);
        config.setConfig("Fall_Effect.Speed", 0.9);
        config.setConfig("ConsoleMessages", true);
        config.setLang("commandGetName", "&6Your'e looking the chest &b[Chest]");
        if(!config.getLang().getStringList("help").toString().contains("getname")){
        	Bukkit.broadcastMessage(config.getLang().getStringList("help").toString());
        	List<String> help = config.getLang().getStringList("help");
        	help.add("&a/lc getname &b: get the name of the targeted LootChest");
        	config.getLang().set("help", help);
        	config.saveLang();
        }
        if(config.getConfig().isSet("Optionnal_Color_If_ArmorStand_Head_Is_Wool")) {
        	config.getConfig().set("Fall_Effect.Optionnal_Color_If_Block_Is_Wool",config.getConfig().getString("Optionnal_Color_If_ArmorStand_Head_Is_Wool") );
        	config.getConfig().set("Optionnal_Color_If_ArmorStand_Head_Is_Wool", null);
        	config.getConfig().set("Fall_Effect.Block", config.getConfig().getString("Armor_Stand_Head_Item"));
        	config.getConfig().set("Armor_Stand_Head_Item", null);
        	config.getConfig().set("Use_ArmorStand_Instead_Of_Block", null);
        	config.getConfig().set("Fall_Effect.Let_Block_Above_Chest_After_Fall", config.getConfig().getBoolean("Let_ArmorStand_On_Chest_After_Fall"));
        	config.getConfig().set("Let_ArmorStand_On_Chest_After_Fall", null);
        	config.saveConfig();
        }
        if(config.getConfig().isSet("Fall_Effect_Height")){
        	config.getConfig().set("Fall_Effect_Height", null);
        	config.getConfig().set("Fall_Effect_Block", null);
        	config.getConfig().set("Enable_fall_effect", null);
        	config.saveConfig();
        }
        
        
        /*

        config.setConfig("Fall_Effect_Block", "NOTE_BLOCK");
       
        config.setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");*/

        

        if(config.getConfig().getBoolean("CheckForUpdates")) {
        	logInfo("Checking for update...");
        	Updater.checkversion();
        }
        //initialisation des matériaux dans toutes les verions du jeu
        //initializing materials in all game versions, to allow cross-version compatibility
        logInfo("Starting particles...");
        Mat.init_materials();
        

    		initParticles();
        
        //One particle was created in 1.13 so that other versions won't have it. Let's remove it if you're not in 1.13
        if (!Minecraft.VERSION.newerThan(v1_12_R1)) {
        	particules[21] = ParticleEffect.valueOf("FOOTSTEP");
        }



        //Initialisation des particules
        //Particle initialization
  
    	//loop de tous les coffres tous les 1/4 (modifiable dans la config) de secondes pour faire spawn des particules
    	//loop of all chests every 1/4 (editable in config) of seconds to spawn particles 
    	new BukkitRunnable() {
    		public void run() {
    			double radius = config.getConfig().getDouble("Particles.radius");
    			if (config.getConfig().getBoolean("Particles.enable")) {
    				for(Location keys : part.keySet()) {
    					int players = 0;
    					if(Minecraft.VERSION.olderThan(v1_8_R1)) {
    						players = org.bukkit.Bukkit.getOnlinePlayers().toArray().length;
    					}else {
    						players = org.bukkit.Bukkit.getOnlinePlayers().size();
    					}
    					if(part.get(keys) != ParticleEffect.REDSTONE && players>0) {
    						if(Minecraft.VERSION.newerThan(v1_12_R1)) {
    							keys.getWorld().spawnParticle( (org.bukkit.Particle) part.get(keys), keys, config.getConfig().getInt("Particles.number"), radius, radius, radius, config.getConfig().getDouble("Particles.speed"));
        					} 
    						else{
    							((ParticleEffect) part.get(keys)).send(keys.getWorld().getPlayers(), keys, radius, radius, radius, config.getConfig().getDouble("Particles.speed"), config.getConfig().getInt("Particles.number"), 50);
    						}

    					}
    					
    				}
    			}
    		}
    	}.runTaskTimer(this, 0, getConfig().getInt("Particles.respawn_ticks"));
    	logInfo("Loading chests...");
    	long current = (new Timestamp(System.currentTimeMillis())).getTime();
		for(String keys : config.getData().getConfigurationSection("chests").getKeys(false)) {
			if(org.bukkit.Bukkit.getWorld(config.getData().getString("chests." + keys + ".position.world")) != null) {
					LootChest.put(keys, new Lootchest(keys));
			}
			else {
    			getLogger().info("§cCouldn't load chest "+keys +" : the world " + config.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
			}
    	}
		logInfo("Loaded "+LootChest.size() + " Lootchests in "+((new Timestamp(System.currentTimeMillis())).getTime()-current) + " miliseconds");
		logInfo("Starting LootChest timers asynchronously...");
		for (final Lootchest lc : Main.LootChest.values()) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(getInstance(), () -> {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            if (!Main.utils.restoreChest(lc, false)) {
                                Main.utils.sheduleRespawn(lc);
                            }
                            Main.utils.reactivateEffects(lc);
                    }, 0L);
            }, 5L);
        }
    	logInfo("Plugin loaded");
    		/*if(!config.getData().isSet("chests." + keys + ".time") ) {
    			config.getData().set("chests." + keys, null);
				config.reloadData();
    		}*/
 
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
		Object parti[];
		if(Minecraft.VERSION.newerThan(v1_12_R1)) {
			parti = new org.bukkit.Particle[] {org.bukkit.Particle.EXPLOSION_HUGE, org.bukkit.Particle.EXPLOSION_LARGE, org.bukkit.Particle.EXPLOSION_NORMAL, org.bukkit.Particle.FIREWORKS_SPARK, org.bukkit.Particle.WATER_BUBBLE, org.bukkit.Particle.SUSPENDED, org.bukkit.Particle.TOWN_AURA, org.bukkit.Particle.CRIT, org.bukkit.Particle.CRIT_MAGIC, org.bukkit.Particle.SMOKE_NORMAL, org.bukkit.Particle.SMOKE_LARGE, org.bukkit.Particle.SPELL_MOB, org.bukkit.Particle.SPELL_MOB_AMBIENT, org.bukkit.Particle.SPELL, org.bukkit.Particle.SPELL_INSTANT, org.bukkit.Particle.SPELL_WITCH, org.bukkit.Particle.NOTE, org.bukkit.Particle.PORTAL, org.bukkit.Particle.ENCHANTMENT_TABLE, org.bukkit.Particle.FLAME, org.bukkit.Particle.LAVA, org.bukkit.Particle.LAVA, org.bukkit.Particle.WATER_SPLASH, org.bukkit.Particle.WATER_WAKE, org.bukkit.Particle.CLOUD, org.bukkit.Particle.SNOWBALL, org.bukkit.Particle.DRIP_WATER, org.bukkit.Particle.DRIP_LAVA, org.bukkit.Particle.SNOW_SHOVEL, org.bukkit.Particle.SLIME, org.bukkit.Particle.HEART, org.bukkit.Particle.VILLAGER_ANGRY, org.bukkit.Particle.VILLAGER_HAPPY, org.bukkit.Particle.BARRIER};

		}else {
			parti = new ParticleEffect[] {ParticleEffect.EXPLOSION_HUGE, ParticleEffect.EXPLOSION_LARGE, ParticleEffect.EXPLOSION_NORMAL, ParticleEffect.FIREWORKS_SPARK, ParticleEffect.WATER_BUBBLE, ParticleEffect.SUSPENDED, ParticleEffect.TOWN_AURA, ParticleEffect.CRIT, ParticleEffect.CRIT_MAGIC, ParticleEffect.SMOKE_NORMAL, ParticleEffect.SMOKE_LARGE, ParticleEffect.SPELL_MOB, ParticleEffect.SPELL_MOB_AMBIENT, ParticleEffect.SPELL, ParticleEffect.SPELL_INSTANT, ParticleEffect.SPELL_WITCH, ParticleEffect.NOTE, ParticleEffect.PORTAL, ParticleEffect.ENCHANTMENT_TABLE, ParticleEffect.FLAME, ParticleEffect.LAVA, ParticleEffect.LAVA, ParticleEffect.WATER_SPLASH, ParticleEffect.WATER_WAKE, ParticleEffect.CLOUD, ParticleEffect.SNOWBALL, ParticleEffect.DRIP_WATER, ParticleEffect.DRIP_LAVA, ParticleEffect.SNOW_SHOVEL, ParticleEffect.SLIME, ParticleEffect.HEART, ParticleEffect.VILLAGER_ANGRY, ParticleEffect.VILLAGER_HAPPY, ParticleEffect.BARRIER};
		}
		for(int i = 0; i<parti.length; i++) {
			particules[i] = parti[i];
		}
	}
}
