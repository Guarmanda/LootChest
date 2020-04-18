package fr.black_eyes.lootchest;

import static org.inventivetalent.reflection.minecraft.Minecraft.Version.*;

import java.util.HashMap;
import org.bukkit.Location;
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
	
    
	
	public void onEnable() {
		instance = this;
		config = new Config();
		utils = new Utils();
		
		
		if(!config.initFiles()) {
        	getLogger().info("§cThe data file couldn't be initialised, the plugin will stop.");
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
        /*

        config.setConfig("Fall_Effect_Block", "NOTE_BLOCK");
        config.setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");*/


        if(config.getConfig().getBoolean("CheckForUpdates")) {
        	Updater.checkversion();
        }
        //initialisation des matériaux dans toutes les verions du jeu
        //initializing materials in all game versions, to allow cross-version compatibility
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
        

		for(String keys : config.getData().getConfigurationSection("chests").getKeys(false)) {
			if(org.bukkit.Bukkit.getWorld(config.getData().getString("chests." + keys + ".position.world")) != null) {
					LootChest.put(keys, new Lootchest(keys));
			}
			else {
    			getLogger().info("§cCouldn't load chest "+keys +" : the world " + config.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
			}
    	}
    	for(Lootchest lc : LootChest.values()) {
    		/*if(!config.getData().isSet("chests." + keys + ".time") ) {
    			config.getData().set("chests." + keys, null);
				config.reloadData();
    		}*/
    		if (!utils.restoreChest(lc, false)) {
				utils.sheduleRespawn(lc);
			}
			utils.reactivateEffects(lc);
	}
    		

    	
  
            
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
