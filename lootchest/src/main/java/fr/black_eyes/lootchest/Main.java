package fr.black_eyes.lootchest;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.*;

import fr.black_eyes.lootchest.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import eu.decentholo.holograms.DecentHologramsPlugin;
import eu.decentholo.holograms.api.DecentHolograms;
import fr.black_eyes.lootchest.commands.CommandHandler;
import fr.black_eyes.lootchest.listeners.DeleteListener;
import fr.black_eyes.lootchest.listeners.UiListener;
import fr.black_eyes.lootchest.particles.Particle;
import fr.black_eyes.lootchest.ui.UiHandler;
import fr.black_eyes.simpleJavaPlugin.SimpleJavaPlugin;
import fr.black_eyes.simpleJavaPlugin.Updater;
import fr.black_eyes.simpleJavaPlugin.Utils;
import lombok.Getter;
import lombok.Setter;

import static fr.black_eyes.lootchest.Constants.DATA_CHEST_PATH;


public class Main extends SimpleJavaPlugin {
	public static final String MENU_MAIN_TYPE = "Menu.main.type";
	public static final String MENU_CHANCES_LORE = "Menu.chances.lore";
	@Getter private Particle[] supportedParticles = {};
	@Getter private final HashMap<Location, Long> protection = new HashMap<>();
	@Getter private final HashMap<String, Particle> particles = new HashMap<>();
	@Getter private final HashMap<Location, Particle> part = new HashMap<>();
	@Setter public static Config configs;
	@Getter private HashMap<String, Lootchest> lootChest;
	@Getter @Setter private static Main instance;
	@Getter private LootChestUtils utils;
	@Getter private boolean useArmorStands;
	@Getter private DecentHologramsPlugin hologramPlugin;
	@Getter private DecentHolograms hologramImpl;
	@Getter private UiHandler uiHandler;
	private static int version = 0;
	

	@Override
	public void onDisable() {
		super.onDisable();
		hologramPlugin.onDisable();
		LootChestUtils.saveAllChests();
	}
    
    /**
     * Check if bungee is enabled in spigot config
     * @return true if bungee is enabed, else false
     */
    private boolean hasBungee(){
        boolean bungee = org.spigotmc.SpigotConfig.bungee;
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        return (bungee && !onlineMode);
    }
	
	/**
	 * Returns the version of your server (the x in 1.x.y)
	 * 
	 * @return The version number
	 */
	public static int getVersion() {
		if(version == 0) {
			String completeVer = Bukkit.getBukkitVersion().split("-")[0];
			// version can be 1.8.4 or 1.12.2 or 1.8, we need to get all the digits after the first dot, and ignore the second dot IF THERE IS ONE
			version = Integer.parseInt(completeVer.split("\\.")[1]);
		}
		return version;
	}

	/**
	 * Get the version a different way:
	 * 1.8.4 = 184, 1.20.6 = 1206, etc.
	 * @return the version number
	 */
	public static int getCompleteVersion(){
		String completeVer = Bukkit.getBukkitVersion().split("-")[0];
		String sversion = completeVer.replace(".", "");
		if(sversion.startsWith("18") || sversion.startsWith("19") || sversion.startsWith("17")){
			//add a 0 between the first and second digit
			sversion = sversion.charAt(0) + "0" + sversion.substring(1);
			if(sversion.endsWith("10")) {
				//remove the 0 at the end
				sversion = sversion.substring(0, sversion.length()-1);
			}
		}
		if(sversion.length() == 3) {
			//add a 0 at the end
			sversion = sversion + "0";
		}
		// we just have to remove the dots and parse string as integer
		return Integer.parseInt(sversion);
	}
    
	@Override
	public void onEnable() {
		setInstance(this);

		if(hologramPlugin == null && getCompleteVersion() >= 1080){
			hologramPlugin = new DecentHologramsPlugin();
		}
		if (getCompleteVersion() >= 1080){
			hologramPlugin.onLoad(this);
			hologramImpl = hologramPlugin.onEnable();
		}

		lootChest = new HashMap<>();
		useArmorStands = true;
		//initialisation des matériaux dans toutes les verions du jeu
        //initializing materials in all game versions, to allow cross-version compatibility
        Mat.init_materials();
		

        //In many versions, I add some text a config option. These lines are done to update config and language files without erasing options that are already set
		super.onEnable();
		if(configFiles.getLang() == null) {
			Utils.logInfo("&cConfig or data files couldn't be initialized, the plugin will stop.");
			return;
		}
		Utils.logInfo("config files loaded");
		Utils.logInfo("Server version: 1." + getVersion() );
		updateOldConfig();
		configFiles.reloadConfig();
		utils = new LootChestUtils();

		int pluginId = 21246; // <-- Replace with the id of your plugin!
		try{ 
       		new Metrics(this, pluginId);
			Utils.logInfo("Metrics started");
		}catch (NoClassDefFoundError e) {
			//if metrics can't be loaded, it's not a big deal
		}		

		uiHandler = new UiHandler(this);
		registerEvents(uiHandler);
		registerCommands();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeChannel());

        
		
		//load config
		setConfigs(Config.getInstance(configFiles.getConfig()));

		//If we enabled bungee broadcast, but we aren't on a bungee server, not any message will show
        if(configs.noteBungeeBroadcast && !hasBungee()){
				Utils.logInfo("&cYou enaled bungee broadcast in config but you didn't enable bungeecord in spigot config!");
				Utils.logInfo("&cSo if this server isn't in a bungee network, no messages will be sent at all on chest spawn!");
        	}
 
        if( !useArmorStands && Main.configs.fallBlock.equals("CHEST")) {
        	configFiles.getConfig().set("Fall_Effect.Block", "NOTE_BLOCK");
        	configs.fallBlock = "NOTE_BLOCK";
        }
        

        if(configs.checkForUpdates) {
        	Utils.logInfo("Checking for update...");
        	new Updater(this, "lootchest.61564");
        }

		//if 1.7, disable world border check
		if(getVersion()<=7) {
			configs.usehologram = false;
			configs.worldborderCheckForSpawn = false;
			Utils.logInfo("&eYou're using 1.7 or below, I disabled worldborder check because worldborder is implemented in spigot from 1.8");
			Utils.logInfo("&eYou're using 1.7 or below, I disabled holograms because it uses armorstands, which are implemented in spigot from 1.8");
					
		}
        Utils.logInfo("Starting particles...");
        
		if(configs.partEnable) {
			//Initialization of particles values, it doesn't spawn them but is used in spawning
			initParticles();
	
			//loop de tous les coffres tous les 1/4 (modifiable dans la config) de secondes pour faire spawn des particules
			//loop of all chests every 1/4 (editable in config) of seconds to spawn particles 
			startParticles();
		}
    	
    	//Loads all chests asynchronously
    	loadChests();
        
	}

	private void registerEvents(UiHandler uiHandler) {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new DeleteListener(), this);
		pluginManager.registerEvents(new UiListener(uiHandler), this);
	}

	private void registerCommands() {
		CommandHandler cmdHandler = new CommandHandler(this, "lootchest");
		String commandsPackage = "fr/black_eyes/lootchest/commands/commands/";
		// get all class names in the commands package instead of hardcoding them
		for (String command : LootChestUtils.getClassesFromJARFile("fr/black_eyes/lootchest/commands/commands/")) {
            try {
                cmdHandler.addSubCommand((SubCommand) Class.forName(commandsPackage.replace("/", ".") + command).getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                Utils.logInfo("&cError while registering command " + command);
            }
        }

	}
	
	/**
	 * Loop all chests every 1/4 of second (configurable in config.yml) and spawns particles around it.
	 * Servers with bad performances (or with 400 chests) should disable particles.
	 */
	private void startParticles() {
		new Thread(() -> {
			if(Main.getCompleteVersion()>=1080){
				new BukkitRunnable() {
					public void run() {
						try{
							float radius = (float) configs.PART_radius;
							float speed = (float)configs.PART_speed;
							int number = configs.partNumber;
							if (configs.partEnable) {
								for(Map.Entry<Location, Particle> entry: part.entrySet()) {
									boolean loaded = entry.getKey().getWorld().isChunkLoaded((int)entry.getKey().getX()/16, (int)entry.getKey().getZ()/16) ;
									if (loaded && entry.getValue()!=null)
											try{
												entry.getValue().display(radius, radius, radius, speed, number, entry.getKey(), entry.getKey().getWorld().getPlayers());
											}catch(Exception e) {
												// concurrent modification exception, just ignore it
											}

								}
							}
						}catch(Exception e) {
							// concurrent modification exception, just ignore it
						}
					}
				}.runTaskTimer(this, 0, configs.partRespawnTicks);
			}else{
				new BukkitRunnable() {
					public void run() {
						float radius = (float) configs.PART_radius;
						float speed = (float)configs.PART_speed;
						int number = configs.partNumber;
						if (configs.partEnable) {
							for(Map.Entry<Location, Particle> entry: part.entrySet()) {
								boolean loaded = entry.getKey().getWorld().isChunkLoaded((int)entry.getKey().getX()/16, (int)entry.getKey().getZ()/16) ;
								if (loaded && entry.getValue()!=null)
									entry.getValue().display(radius, radius, radius, speed, number, entry.getKey(), entry.getKey().getWorld().getPlayers());
								
							}
						}
					}
				}.runTaskTimer(this, 0, configs.partRespawnTicks);
			}
		}).start();
	}
    		
	/**
	 * Loads all chests asynchronously
	 */
	@SuppressWarnings("deprecation") //compatibility with 1.7
	private void loadChests() {
		long countdown = configs.cooldownBeforePluginStart;
    	if(countdown>0) 
			Utils.logInfo("Chests will load in "+ countdown + " seconds.");
    	
        this.getServer().getScheduler().runTaskLater(this, () -> {
            Utils.logInfo("Loading chests...");
            long current = (new Timestamp(System.currentTimeMillis())).getTime();
            for(String keys : Objects.requireNonNull(configFiles.getData().getConfigurationSection("chests")).getKeys(false)) {
                String name = configFiles.getData().getString(DATA_CHEST_PATH + keys + ".position.world");
                String randomName = name;
                if( configFiles.getData().getInt(DATA_CHEST_PATH + keys + ".randomradius")>0) {
                    randomName = configFiles.getData().getString(DATA_CHEST_PATH + keys + ".randomPosition.world");
                }
                if(name != null && LootChestUtils.isWorldLoaded(randomName) && LootChestUtils.isWorldLoaded(name)) {
                    getLootChest().put(keys, new Lootchest(keys));
                }
                else {
                    Utils.logInfo("&cCouldn't load chest "+keys +" : the world " + configFiles.getData().getString(DATA_CHEST_PATH + keys + ".position.world") + " is not loaded.");
                }
            }
            
            Utils.logInfo("Loaded "+lootChest.size() + " Lootchests in "+((new Timestamp(System.currentTimeMillis())).getTime()-current) + " miliseconds");
            Utils.logInfo("Starting LootChest timers asynchronously...");
            for (final Lootchest lc : lootChest.values()) {
                Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, () ->
                        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                            if (!lc.spawn(false)) {
                                LootChestUtils.scheduleReSpawn(lc);
                                lc.reactivateEffects();
                            }
                        }, 0L)
                        , 5L);
            }
            Utils.logInfo("Plugin loaded");
                }, countdown+20);
	}
	
	
	/**
	* In many versions, I add some text a config option.
	* These lines are done to update config and language files without erasing options that are already set
	*/
	private void updateOldConfig() {
		// hotfix
		// in chances.lore, replace all % by nothing
		if(Objects.requireNonNull(configFiles.getLang().getString(MENU_CHANCES_LORE)).contains("%")) {
			String lore = configFiles.getLang().getString(MENU_CHANCES_LORE);
			if (lore != null) {
				lore = lore.replace("%", "");
			}
			configFiles.getLang().set(MENU_CHANCES_LORE, lore);
		}
		if(configFiles.getConfig().getInt("Particles.respawn_ticks") == 5){
			configFiles.getConfig().set("Particles.respawn_ticks", 20);
		}
		if(configFiles.getConfig().isSet("RemoveChestAfterFirstOpenning")){
			boolean remove = configFiles.getConfig().getBoolean("RemoveChestAfterFirstOpenning");
			configFiles.getConfig().set("RemoveChestAfterFirstOpenning", null);
			configFiles.getConfig().set("RemoveChestAfterFirstOpening", remove);
		}
		configFiles.setConfig("spawn_on_non_solid_blocks", false);
		configFiles.setConfig("Minimum_Height_For_Random_Spawn", 0);
		configFiles.setConfig("Max_Height_For_Random_Spawn", 200);
		configFiles.setConfig("Max_Filled_Slots_By_Default", 0);
		configFiles.setConfig("SaveDataFileDuringReload", true);
		configFiles.setConfig("respawn_notify.respawn_all_with_command_in_world.enabled", true);
		configFiles.setConfig("respawn_notify.respawn_all_with_command_in_world.message", "&6All chests where forced to respawn in world [World]!\n&6Get them guys!");
		configFiles.setConfig("respawn_notify.Minimum_Number_Of_Players_For_Natural_Spawning", 0);
		configFiles.setConfig("EnableLootin", false);
		//deletion of now unsuported feature
		configFiles.getConfig().set("Fall_Effect.Let_Block_Above_Chest_After_Fall", null);
		configFiles.setLang(MENU_MAIN_TYPE, "&1Select Chest Item");
		configFiles.setLang("notAnInteger", "&c[Number] is not an integer!");
		configFiles.setLang("blockIsAlreadyLootchest", "&cThis block is already a LootChest!");
		configFiles.setLang("editedMaxFilledSlots", "&aYou edited the max filled slots of chest &b[Chest]");
		configFiles.setLang("copiedChest", "&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]");
		configFiles.setLang("NotEnoughPlayers", "&cThe server needs at least [Number] players to spawn chests");
		configFiles.setLang("ChestDespawned", "&aChest &b[Chest] &asuccesfuly despacned!");
		configFiles.setLang("NoChestAtLocation", "&cThe specified lootchest was already destroyed.");
		if(configFiles.getConfig().isSet("Fall_Effect.Optionnal_Color_If_Block_Is_Wool"))
			configFiles.setConfig("Fall_Effect.Optionnal_Color_If_Block_Is_Wool", null);
		configFiles.setLang("AllChestsDespawned", "&aAll chests were despawned!");
		configFiles.setLang("AllChestsDespawnedInWorld", "&aAll chests were despawned in world [World]!");
		configFiles.setLang("worldDoesntExist", "&cThe world [World] doesn't exist!");
		configFiles.setLang("AllChestsReloadedInWorld", "&aAll chests reloaded in world [World]!");
		if(!configFiles.getLang().getStringList("help").toString().contains("despawnall")){
			List<String> help = configFiles.getLang().getStringList("help");
			help.add("&a/lc despawnall [world] &b: despawns all chests, optionally in a specific world");
			configFiles.getLang().set("help", help);
			configFiles.saveLang();
		}
		if(!configFiles.getLang().getStringList("help").toString().contains("copy")){
			List<String> help = configFiles.getLang().getStringList("help");
			help.add("&a/lc copy <source> <dest> &b: copy a chest into another");
			configFiles.getLang().set("help", help);
			configFiles.saveLang();
		}
		if(!configFiles.getLang().getStringList("help").toString().contains("maxfilledslots")){
			List<String> help = configFiles.getLang().getStringList("help");
			help.add("&a/lc maxfilledslots <name> <number> &b: set the max filled slots of a chest");
			configFiles.getLang().set("help", help);
			configFiles.saveLang();
		}
		if(!configFiles.getLang().getStringList("help").toString().contains("despawn ")){
			  List<String> help = configFiles.getLang().getStringList("help");
			  help.add("&a/lc despawn <name> &b: despawns a chest");
			  configFiles.getLang().set("help", help);
			  configFiles.saveLang();
		}
		//remove useless command
		if(configFiles.getLang().getStringList("help").toString().contains("removeAllHolo")){
			List<String> help = configFiles.getLang().getStringList("help");
			//get line and remove it
			int index = help.stream().filter(s -> s.contains("removeAllHolo")).findFirst().map(help::indexOf).orElse(-1);
			if(index!=-1) {
				help.remove(index);
			}
			configFiles.getLang().set("help", help);
			configFiles.saveLang();
		}
		configFiles.saveLang();
		configFiles.saveConfig();

	}
	

	
	/**
	 * This initializes an array of particles. Under 1.12, I use InventiveTalent's ParticleAPI,
	 * and for 1.12+, I use new particles spawning functions, so I use default spigot particles
	 */
	private void initParticles() {
		int cpt = 0;
		for(Particle p:Particle.values()) {
			if(p.isSupported() && p.getParticle()!=null) {
				cpt++;
			}
		}
		supportedParticles = new Particle[cpt];
		int i = 0;
		for(Particle p:Particle.values()) {
			if(p.isSupported() && p.getParticle()!=null) {
				particles.put(p.getName(), p);
				supportedParticles[i++] = p;
			}
		}
	}


}
