package fr.black_eyes.lootchest;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Particle;
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
	
	
	public static HashMap<Location, Particle> part = new HashMap<Location, Particle>();
	private File dataFile;
	private FileConfiguration data;
	private File configFile;
	private FileConfiguration config;
	private File langFile;
	private FileConfiguration lang;
	private static Main instance;
	
	public void onEnable() {
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Utils(), this);
		this.getServer().getPluginManager().registerEvents(new DeleteListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        this.getCommand("lootchest").setExecutor(new Lootchest());
        super.onEnable();
        initFiles();
        
        //Initialisation des particules
        for(String keys : getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
        	final Location loc2 = (Location) Main.getInstance().getData().get("chests." + keys + ".location");
        	final Location loc = new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ());
    		loc.setX(loc.getX()+0.5);
    		loc.setY(loc.getY()+0.5);
    		loc.setZ(loc.getZ()+0.5);
    		Particle particules[] = {Particle.EXPLOSION_HUGE, Particle.EXPLOSION_LARGE, Particle.EXPLOSION_NORMAL, Particle.FIREWORKS_SPARK, Particle.WATER_BUBBLE, Particle.SUSPENDED, Particle.TOWN_AURA, Particle.CRIT, Particle.CRIT_MAGIC, Particle.SMOKE_NORMAL, Particle.SMOKE_LARGE, Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT, Particle.SPELL, Particle.SPELL_INSTANT, Particle.SPELL_WITCH, Particle.NOTE, Particle.PORTAL, Particle.ENCHANTMENT_TABLE, Particle.FLAME, Particle.LAVA, Particle.FOOTSTEP, Particle.WATER_SPLASH, Particle.WATER_WAKE, Particle.CLOUD, Particle.REDSTONE, Particle.SNOWBALL, Particle.DRIP_WATER, Particle.DRIP_LAVA, Particle.SNOW_SHOVEL, Particle.SLIME, Particle.HEART, Particle.VILLAGER_ANGRY, Particle.VILLAGER_HAPPY, Particle.BARRIER};
    		for(Particle part : particules) {
    			if((""+part).contains(Main.getInstance().getData().getString("chests." + keys + ".particle")))
    				Main.part.put(loc, part);
    		}

    	}
        
        //loop de tous les coffres tous les 1/4 de secondes pour faire spawn des particules
        new BukkitRunnable() {
            public void run() {
            	double radius = getConfig().getDouble("Particles.radius");
            	for(Location keys : part.keySet()) {
            		keys.getWorld().spawnParticle(part.get(keys), keys, getConfig().getInt("Particles.number"), radius, radius, radius, getConfig().getDouble("Particles.speed"));
            	}
            }
        }.runTaskTimer(this, 0, getConfig().getInt("Particles.respawn_ticks"));
        
        //check du respawn des coffres toutes les minutes
        new BukkitRunnable() {
            public void run() {
            	long tempsactuel = (new Timestamp(System.currentTimeMillis())).getTime();
            	for(String keys : getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
            		long minutes = getInstance().getData().getLong("chests." + keys + ".time")*60*1000;
            		long tempsenregistre = getInstance().getData().getLong("chests." + keys + ".lastreset");
            		if(tempsactuel - tempsenregistre > minutes) {
            			Utils.restoreChest(keys);
            		}
            		
            	}
            }
        }.runTaskTimer(this, 0, 600);
    }
	public static Main getInstance() {
        return instance;
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
	
	private void initFiles() {
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
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	}
}
