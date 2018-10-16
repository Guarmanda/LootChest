package fr.black_eyes.lootchest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
	public static Particle particules[] = {Particle.EXPLOSION_HUGE, Particle.EXPLOSION_LARGE, Particle.EXPLOSION_NORMAL, Particle.FIREWORKS_SPARK, Particle.WATER_BUBBLE, Particle.SUSPENDED, Particle.TOWN_AURA, Particle.CRIT, Particle.CRIT_MAGIC, Particle.SMOKE_NORMAL, Particle.SMOKE_LARGE, Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT, Particle.SPELL, Particle.SPELL_INSTANT, Particle.SPELL_WITCH, Particle.NOTE, Particle.PORTAL, Particle.ENCHANTMENT_TABLE, Particle.FLAME, Particle.LAVA, Particle.LAVA, Particle.WATER_SPLASH, Particle.WATER_WAKE, Particle.CLOUD, Particle.REDSTONE, Particle.SNOWBALL, Particle.DRIP_WATER, Particle.DRIP_LAVA, Particle.SNOW_SHOVEL, Particle.SLIME, Particle.HEART, Particle.VILLAGER_ANGRY, Particle.VILLAGER_HAPPY, Particle.BARRIER};;
	public static Material ender_eye;
	public static Material watch;
	public static Material mycelium;
	public static Material firework;
	public static Material ender_portal_frame;
	public static Material snowball;
	public static Material iron_shovel;
	public static Material red_rose;
	public static Material enchant_table;
	
	public void onDisable() {
		try {
			Main.getInstance().getConfig().save(Main.getInstance().getConfigF());
			Main.getInstance().getData().save(Main.getInstance().getDataF());
			Main.getInstance().getLang().save(Main.getInstance().getLangF());
		} catch (IOException e) {
			e.printStackTrace();
		
		}	
	}
	
	public void onEnable() {
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Utils(), this);
		this.getServer().getPluginManager().registerEvents(new DeleteListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        this.getCommand("lootchest").setExecutor(new Lootchest());
        super.onEnable();
        initFiles();
        setConfig("Particles.enable", true);
        setConfig("UseHologram", true);
        setConfig("RemoveEmptyChests", true);
        setLang("PluginReloaded", "&aConfig file, lang, and chest data were reloaded");
        setLang("ListCommand", "&aList of all chests: [List]");
        setLang("help.line10", "&a/lc reload &b: reloads the plugin");
        setLang("help.line11", "&a/lc list &b: list all chests");
        if(Bukkit.getVersion().contains("1.13")){
        
        	particules[21] = Particle.END_ROD;
        	ender_eye = Material.ENDER_EYE;
        	watch = Material.CLOCK;
        	mycelium = Material.MYCELIUM;
        	firework = Material.FIREWORK_ROCKET;
        	ender_portal_frame = Material.END_PORTAL_FRAME;
        	snowball = Material.SNOWBALL;
        	iron_shovel = Material.IRON_SHOVEL;
        	red_rose = Material.ROSE_RED;
        	enchant_table = Material.ENCHANTING_TABLE;
        }
        else if(Bukkit.getVersion().contains("1.8")) {
        	getInstance().getConfig().set("Particles.enable", false);
        	getLogger().info("1.8 detected: particles were disabled");
        }
        else {
        	particules[21] = Particle.valueOf("FOOTSTEP");
        	ender_eye = Material.valueOf("EYE_OF_ENDER");
        	watch = Material.valueOf("WATCH");
        	mycelium = Material.valueOf("MYCEL");
        	firework = Material.valueOf("FIREWORK");
        	ender_portal_frame = Material.valueOf("ENDER_PORTAL_FRAME");
        	snowball = Material.valueOf("SNOW_BALL");
        	iron_shovel = Material.valueOf("IRON_SPADE");
        	red_rose = Material.valueOf("RED_ROSE");
        	enchant_table = Material.valueOf("ENCHANTMENT_TABLE");
        }
        //Initialisation des particules
        
        	for(String keys : getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
        		final Location loc2 = (Location) Main.getInstance().getData().get("chests." + keys + ".location");
        		final Location loc = new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ());
        		loc.setX(loc.getX()+0.5);
        		loc.setY(loc.getY()+0.5);
        		loc.setZ(loc.getZ()+0.5);
        		for(Particle part : particules) {
        			if((""+part).contains(Main.getInstance().getData().getString("chests." + keys + ".particle")))
        				Main.part.put(loc, part);
        		}  		
        	}
        
        	//loop de tous les coffres tous les 1/4 de secondes pour faire spawn des particules
        	new BukkitRunnable() {
        		public void run() {
        			double radius = getConfig().getDouble("Particles.radius");
        			if (getInstance().getConfig().getBoolean("Particles.enable")) {
        			for(Location keys : part.keySet()) {
        				keys.getWorld().spawnParticle(part.get(keys), keys, getConfig().getInt("Particles.number"), radius, radius, radius, getConfig().getDouble("Particles.speed"));
        			}
        			}
        		}
        	}.runTaskTimer(this, 0, getConfig().getInt("Particles.respawn_ticks"));
        
        //check du respawn des coffres toutes les minutes
        new BukkitRunnable() {
            public void run() {
            	for(String keys : getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
            			Utils.restoreChest(keys);
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
