package fr.black_eyes.lootchest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import fr.black_eyes.lootchest.commands.Lootchest;
import fr.black_eyes.lootchest.listeners.DeleteListener;
import fr.black_eyes.lootchest.listeners.InventoryListeners;




//Faire commande de reload
//initialiser menu temps



public class Main extends JavaPlugin{
	
	public static Object particules[] = new Object[35];
	public static HashMap<Location, Object> part = new HashMap<Location, Object>();
	private File dataFile;
	private FileConfiguration data;
	private File configFile;
	private FileConfiguration config;
	private File langFile;
	private FileConfiguration lang;
	private static Main instance;

	
	public void onDisable() {
		try {
			Main.getInstance().getConfig().save(Main.getInstance().getConfigF());
			Main.getInstance().getData().save(Main.getInstance().getDataF());
			Main.getInstance().getLang().save(Main.getInstance().getLangF());
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
		
		}	
	}
	
	public void onEnable() {
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Utils(), this);
		this.getServer().getPluginManager().registerEvents(new DeleteListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        this.getCommand("lootchest").setExecutor(new Lootchest());
        this.getCommand("lootchest").setTabCompleter(new Lootchest());
        super.onEnable();
        if(!initFiles()) {
        	getLogger().info("§cThe data file couldn't be initialised, This is, in most cases, due to bad chest locations. Please, remove the chests wich are in unexisting worlds");
        	return;
        }
        setConfig("Particles.enable", true);
        setConfig("UseHologram", true);
        setConfig("RemoveEmptyChests", true);
        setConfig("RemoveChestAfterFirstOpenning", false);
        setConfig("respawn_notify.natural_respawn.enabled", true);
        setConfig("respawn_notify.respawn_with_command.enabled", true);
        setConfig("respawn_notify.respawn_all_with_command.enabled", true);
        setConfig("respawn_notify.natural_respawn.message", "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!");
        setConfig("respawn_notify.respawn_with_command.message", "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!");
        setConfig("respawn_notify.respawn_all_with_command.message", "&6All chests where forced to respawn! Get them guys!");
        setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");
        setLang("ListCommand", "&aList of all chests: [List]");
        setLang("help.line10", "&a/lc reload &b: reloads the plugin");
        setLang("help.line11", "&a/lc list &b: list all chests");
        setLang("Menu.main.copychest", "&1Copy settings from anyther chest");
        setLang("Menu.copy.name", "&1Choose a chest to copy its settings");
        setLang("copiedChest", "&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]");
        setLang("changedPosition", "&6You set the location of chest &b[Chest] &6to your location");
        setLang("help.line12", "&a/lc setpos &b: edit the position of a chest");
        setLang("settime", "&6You successfully set the time of the chest &b[Chest]");
        setLang("Menu.time.infinite", "&6Desactivates the respawn time");
        if(!Bukkit.getVersion().contains("1.8")) {
    		initParticles();
        }
        if(Bukkit.getVersion().contains("1.8")) {
        	getInstance().getConfig().set("Particles.enable", false);
        	getLogger().info("Spigot 1.8 detected: particles were disabled");
        }

        else if (!Bukkit.getVersion().contains("1.13")) {
        	particules[21] = org.bukkit.Particle.valueOf("FOOTSTEP");
        }

        //Transformation des anciennes positions pour éviter les erreurs de fichiers
        for(String keys : getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
        	if(!getInstance().getData().isSet("chests." + keys + ".position")) {
        		if(getInstance().getData().isSet("chests." + keys + ".location")) {
        			final Location loc = (Location) Main.getInstance().getData().get("chests." + keys + ".location");
        			Utils.setPosition(keys, loc);
        			getInstance().getData().set("chests." + keys + ".location", null);
        			getLogger().info("Updated location of chest " + keys +" to new location format to prevent errors when loading data file"); 
        			try {
        				getInstance().getData().save(Main.getInstance().getDataF());
        				getInstance().getData().load(Main.getInstance().getDataF());
        			} catch (IOException | InvalidConfigurationException e) {
        				e.printStackTrace();
        			}
        		}
        		else {
        			Utils.deleteChest(keys);
        		}
        	}
        }
        //Initialisation des particules
        if(!Bukkit.getVersion().contains("1.8")) {    
        	//loop de tous les coffres tous les 1/4 de secondes pour faire spawn des particules
        	new BukkitRunnable() {
        		public void run() {
        			double radius = getConfig().getDouble("Particles.radius");
        			if (getInstance().getConfig().getBoolean("Particles.enable")) {
        				for(Location keys : part.keySet()) {
        				keys.getWorld().spawnParticle( (org.bukkit.Particle) part.get(keys), keys, getConfig().getInt("Particles.number"), radius, radius, radius, getConfig().getDouble("Particles.speed"));
        				}
        			}
        		}
        	}.runTaskTimer(this, 0, getConfig().getInt("Particles.respawn_ticks"));
        }
        //check du respawn des coffres toutes les minutes
        new BukkitRunnable() {
            public void run() {
            	for(String keys : getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
            		if(Utils.getPosition(keys).getWorld() != null) {
            			Utils.restoreChest(keys, false);
            		}
            		else {
            			getLogger().info("§cCouldn't load chest "+keys +" : the world " + getInstance().getData().getString("chests." + keys + ".position.world") + " is not loaded");
            		}
            	}
            }
        }.runTaskTimer(this, 0, 600);
    }
	public static Main getInstance() {
        return instance;
    }
	
	public void setConfig(String path, Object value) {
		if(this.getConfig().isSet(path))
			return;
		else
			getInstance().getConfig().set(path, value);
			try {
				Main.getInstance().getConfig().save(Main.getInstance().getConfigF());
				Main.getInstance().getConfig().load(Main.getInstance().getConfigF());
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
	}
	
	
	public void setLang(String path, Object value) {
		if(this.getLang().isSet(path))
			return;
		else
			getInstance().getLang().set(path, value);
			try {
				Main.getInstance().getLang().save(Main.getInstance().getLangF());
				Main.getInstance().getLang().load(Main.getInstance().getLangF());
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
	}
	public File getDataF() {
		return this.dataFile;
	}
	public File getConfigF() {
		return this.configFile;
	}
	public File getLangF() {
		return this.langFile;
	}
	public FileConfiguration getData() {
		return this.data;
	}
	public FileConfiguration getConfig() {
		return this.config;
	}
	public FileConfiguration getLang() {
		return this.lang;
	}
	
	private boolean initFiles() {
		//config
	    configFile = new File(getDataFolder(), "config.yml");
	    langFile = new File(getDataFolder(), "lang.yml");
	    dataFile = new File(getDataFolder(), "data.yml");
	    if (!configFile.exists()) {
	        configFile.getParentFile().mkdirs();
	        saveResource("config.yml", false);
	    }
	    config= new YamlConfiguration();
	    try {
	        config.load(configFile);
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	    
	    //lang
	    if (!langFile.exists()) {
	        langFile.getParentFile().mkdirs();
	        saveResource("lang.yml", false);
	    }
	    lang= new YamlConfiguration();
	    try {
	        lang.load(langFile);
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	   
	    //data
	    if (!dataFile.exists()) {
	        dataFile.getParentFile().mkdirs();
	        saveResource("data.yml", false);
	    }
	    data= new YamlConfiguration();
	    try {
	        data.load(dataFile);
	    } catch ( Exception e) {
	        return false;
	    }
		return true;
	}
	
	private void initParticles() {
		org.bukkit.Particle parti[] = {org.bukkit.Particle.EXPLOSION_HUGE, org.bukkit.Particle.EXPLOSION_LARGE, org.bukkit.Particle.EXPLOSION_NORMAL, org.bukkit.Particle.FIREWORKS_SPARK, org.bukkit.Particle.WATER_BUBBLE, org.bukkit.Particle.SUSPENDED, org.bukkit.Particle.TOWN_AURA, org.bukkit.Particle.CRIT, org.bukkit.Particle.CRIT_MAGIC, org.bukkit.Particle.SMOKE_NORMAL, org.bukkit.Particle.SMOKE_LARGE, org.bukkit.Particle.SPELL_MOB, org.bukkit.Particle.SPELL_MOB_AMBIENT, org.bukkit.Particle.SPELL, org.bukkit.Particle.SPELL_INSTANT, org.bukkit.Particle.SPELL_WITCH, org.bukkit.Particle.NOTE, org.bukkit.Particle.PORTAL, org.bukkit.Particle.ENCHANTMENT_TABLE, org.bukkit.Particle.FLAME, org.bukkit.Particle.LAVA, org.bukkit.Particle.LAVA, org.bukkit.Particle.WATER_SPLASH, org.bukkit.Particle.WATER_WAKE, org.bukkit.Particle.CLOUD, org.bukkit.Particle.REDSTONE, org.bukkit.Particle.SNOWBALL, org.bukkit.Particle.DRIP_WATER, org.bukkit.Particle.DRIP_LAVA, org.bukkit.Particle.SNOW_SHOVEL, org.bukkit.Particle.SLIME, org.bukkit.Particle.HEART, org.bukkit.Particle.VILLAGER_ANGRY, org.bukkit.Particle.VILLAGER_HAPPY, org.bukkit.Particle.BARRIER};
		for(int i = 0; i<parti.length; i++) {
			particules[i] = parti[i];
		}
	}
}
