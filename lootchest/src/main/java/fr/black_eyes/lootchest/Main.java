package fr.black_eyes.lootchest;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import eu.decentholo.holograms.DecentHologramsPlugin;
import eu.decentholo.holograms.api.DecentHolograms;
import fr.black_eyes.lootchest.commands.CommandHandler;
import fr.black_eyes.lootchest.commands.commands.CopyCommand;
import fr.black_eyes.lootchest.commands.commands.CreateCommand;
import fr.black_eyes.lootchest.commands.commands.DespawnAllCommand;
import fr.black_eyes.lootchest.commands.commands.EditCommand;
import fr.black_eyes.lootchest.commands.commands.GetNameCommand;
import fr.black_eyes.lootchest.commands.commands.GiveCommand;
import fr.black_eyes.lootchest.commands.commands.ListCommand;
import fr.black_eyes.lootchest.commands.commands.LocateCommand;
import fr.black_eyes.lootchest.commands.commands.MaxFilledSlotsCommand;
import fr.black_eyes.lootchest.commands.commands.RandomSpawnCommand;
import fr.black_eyes.lootchest.commands.commands.ReloadCommand;
import fr.black_eyes.lootchest.commands.commands.RemoveCommand;
import fr.black_eyes.lootchest.commands.commands.RespawnAllCommand;
import fr.black_eyes.lootchest.commands.commands.RespawnCommand;
import fr.black_eyes.lootchest.commands.commands.SetHoloCommand;
import fr.black_eyes.lootchest.commands.commands.SetPosCommand;
import fr.black_eyes.lootchest.commands.commands.SetProtectionCommand;
import fr.black_eyes.lootchest.commands.commands.SetTimeCommand;
import fr.black_eyes.lootchest.commands.commands.ToggleFallCommand;
import fr.black_eyes.lootchest.commands.commands.TpCommand;
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
	private static int version = 0;

	
	//the way holograms are working changed a lot since 2.2.4. 
	//If user just done the update, this will be auto set to true by detecting a lacking config option
	//that appeared precisely in 2.2.4
	@Getter private boolean killOldHolograms = false;
	

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

		UiHandler uiHandler = new UiHandler(this);
		registerEvents(uiHandler);
		registerCommands(uiHandler);
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

	private void registerCommands(UiHandler uiHandler) {
		CommandHandler cmdHandler = new CommandHandler(this, "lootchest");
		cmdHandler.addSubCommand(new CopyCommand());
		cmdHandler.addSubCommand(new CreateCommand(uiHandler));
		cmdHandler.addSubCommand(new DespawnAllCommand());
		cmdHandler.addSubCommand(new EditCommand(uiHandler));
		cmdHandler.addSubCommand(new GetNameCommand());
		cmdHandler.addSubCommand(new GiveCommand());
		cmdHandler.addSubCommand(new ListCommand());
		cmdHandler.addSubCommand(new LocateCommand());
		cmdHandler.addSubCommand(new MaxFilledSlotsCommand());
		cmdHandler.addSubCommand(new RandomSpawnCommand());
		cmdHandler.addSubCommand(new ReloadCommand());
		cmdHandler.addSubCommand(new RemoveCommand());
		cmdHandler.addSubCommand(new RespawnAllCommand());
		cmdHandler.addSubCommand(new RespawnCommand());
		cmdHandler.addSubCommand(new SetHoloCommand());
		cmdHandler.addSubCommand(new SetPosCommand());
		cmdHandler.addSubCommand(new SetProtectionCommand());
		cmdHandler.addSubCommand(new SetTimeCommand());
		cmdHandler.addSubCommand(new ToggleFallCommand());
		cmdHandler.addSubCommand(new TpCommand());
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
	  configFiles.setConfig("Max_Filled_Slots_By_Default", 0);
	  configFiles.setConfig("SaveDataFileDuringReload", true);
	  configFiles.setConfig("respawn_protection_time_in_second_by_default", 0);
	  configFiles.setConfig("allow_spawning_on_water", false);
	  configFiles.setConfig("Particles.enable", true);
      configFiles.setConfig("PreventHopperPlacingUnderLootChest", true);
      configFiles.setConfig("respawn_notify.per_world_message", true);
      configFiles.setConfig("respawn_notify.message_on_chest_take", true);
	  configFiles.setConfig("respawn_notify.respawn_all_with_command_in_world.enabled", true);
	  configFiles.setConfig("respawn_notify.respawn_all_with_command_in_world.message", "&6All chests where forced to respawn in world [World]!\n&6Get them guys!");
      configFiles.setConfig("Minimum_Number_Of_Players_For_Natural_Spawning", 0);
	  configFiles.setConfig("respawn_notify.Minimum_Number_Of_Players_For_Natural_Spawning", 0);
      configFiles.setConfig("use_players_locations_for_randomspawn", false);
      configFiles.setConfig("Cooldown_Before_Plugin_Start", 0);
      configFiles.setConfig("Prevent_Chest_Spawn_In_Protected_Places", false);
      configFiles.setConfig("WorldBorder_Check_For_Spawn", true);
	  configFiles.setConfig("EnableLootin", false);
	  //deletion of now unsuported feature
	  configFiles.getConfig().set("Fall_Effect.Let_Block_Above_Chest_After_Fall", null);
	  configFiles.setLang("CantBreakBlockBecauseProtected", "&cYou have to wait [Time] seconds to loot that chest!");
	  configFiles.setLang("editedProtectionTime", "&aYou edited the protection time of chest [Chest]");
      configFiles.setLang("Menu.main.disable_fall", "&aFall effect is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_respawn_natural", "&aNatural-respawn message is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_respawn_cmd", "&aCommand-respawn message is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_take_message", "&aMessage on chest take is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.enable_fall", "&cFall effect is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.enable_respawn_natural", "&cNatural-respawn message is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.enable_respawn_cmd", "&cCommand-respawn message is disabled. Click to &aENABLE &cit");
      configFiles.setLang(MENU_MAIN_TYPE, "&1Select Chest Item");
      configFiles.setLang("Menu.type.name", "&1Select Chest Item");
	  configFiles.setLang("notAnInteger", "&c[Number] is not an integer!");
      configFiles.setLang("Menu.main.enable_take_message", "&cMessage on chest take is disabled. Click to &aENABLE &cit");
      configFiles.setLang("locate_command.main_message",  "&6Location of loot chests:");
      configFiles.setLang("editedChestType", "&aEdited type of chest &b[Chest]");
      configFiles.setLang("locate_command.chest_list", "- &b[Chest]&6: [x], [y], [z] in world [world]");
      configFiles.setLang("removedHolograms", "&aSuccessfully removed &b[Number] LootChest holograms.");
      configFiles.setLang("CantOpenLootchestBecauseMonster", "&cYou can't open this chest while there is [Number] monsters nearby");
	  configFiles.setLang("blockIsAlreadyLootchest", "&cThis block is already a LootChest!");
	  configFiles.setLang("editedMaxFilledSlots", "&aYou edited the max filled slots of chest &b[Chest]");
	  configFiles.setLang("copiedChest", "&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]");
	  configFiles.setLang("NotEnoughPlayers", "&cThe server needs at least [Number] players to spawn chests");
      if (configFiles.getLang().isSet("help.line1")) {
          final List<String> tab = new ArrayList<>();
          for (int i = 1; i <= 17; ++i) {
              if (configFiles.getLang().getString("help.line" + i) != null) {
                  tab.add(configFiles.getLang().getString("help.line" + i));
              }
          }
          configFiles.getLang().set("help", tab);
          try {
              configFiles.getLang().save(configFiles.getLangF());
              configFiles.getLang().load(configFiles.getLangF());
          }
          catch (IOException | InvalidConfigurationException e) {
              Utils.logInfo("Error while updating lang.yml");
          }
      }
      configFiles.setConfig("Fall_Effect.Optionnal_Color_If_Block_Is_Wool", null);
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
	  configFiles.setLang("AllChestsDespawned", "&aAll chests were despawned!");
	  configFiles.setLang("AllChestsDespawnedInWorld", "&aAll chests were despawned in world [World]!");
      configFiles.setLang("Menu.time.notInfinite", "&6Reactivate respawn time");
      configFiles.setLang("commandGetName", "&6Your'e looking the chest &b[Chest]");
	  configFiles.setLang("worldDoesntExist", "&cThe world [World] doesn't exist!");
	  configFiles.setLang("AllChestsReloadedInWorld", "&aAll chests reloaded in world [World]!");
      if(!configFiles.getLang().getStringList("help").toString().contains("getname")){
      	List<String> help = configFiles.getLang().getStringList("help");
      	help.add("&a/lc getname &b: get the name of the targeted LootChest");
      	configFiles.getLang().set("help", help);
      	configFiles.saveLang();
      }
	  if(!configFiles.getLang().getStringList("help").toString().contains("despawnall")){
		List<String> help = configFiles.getLang().getStringList("help");
		help.add("&a/lc despawnall [world] &b: despawns all chests, optionally in a specific world");
		configFiles.getLang().set("help", help);
		configFiles.saveLang();
	}
      if(!configFiles.getLang().getStringList("help").toString().contains("locate")){
      	List<String> help = configFiles.getLang().getStringList("help");
      	help.add("&a/lc locate &b: gives locations of all chests that haves natural respawn message enabled");
      	configFiles.getLang().set("help", help);
      	configFiles.saveLang();        	
      }
	  if(!configFiles.getLang().getStringList("help").toString().contains("setprotection")){
    	List<String> help = configFiles.getLang().getStringList("help");
    	help.add("&a/lc setprotection <name> <time> &b: set the spawn protection time in seconds for a chest");
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
    	

      if(!configFiles.getConfig().isSet("Timer_on_hologram.Show_Timer_On_Hologram")) {
    	  boolean timeholo = configFiles.getConfig().getBoolean("Show_Timer_On_Hologram");
    	  configFiles.getConfig().set("Show_Timer_On_Hologram", null);
    	  configFiles.getConfig().set("Timer_on_hologram.Show_Timer_On_Hologram", timeholo);
    	  configFiles.getConfig().set("Timer_on_hologram.Hours_Separator", " hours, ");
    	  configFiles.getConfig().set("Timer_on_hologram.Minutes_Separator", " minutes  and ");
    	  configFiles.getConfig().set("Timer_on_hologram.Seconds_Separator", " seconds.");
    	  configFiles.getConfig().set("Timer_on_hologram.Format", "&3%Hours%Hsep%Minutes%Msep%Seconds%Ssep &bleft for %Hologram to respawn");
      }
      if(configFiles.getLang().getString(MENU_CHANCES_LORE).equals("&aLeft click: +1; right: -1; shift+right: -10; shift+left: +10; tab+right: -50") || configFiles.getLang().getString(MENU_CHANCES_LORE).equals("&aLeft click to up percentage, Right click to down it")) {
      	configFiles.getLang().set(MENU_CHANCES_LORE, "&aLeft click: +1||&aright: -1||&ashift+right: -10||&ashift+left: +10||&atab+right: -50");
      }
      
      if(configFiles.getLang().getString("Menu.main.respawnTime").equals("&1Respawn time editing")) {
        	configFiles.getLang().set("Menu.main.respawnTime", "&1Edit Respawn Time");
       }
      if(configFiles.getLang().getString(MENU_MAIN_TYPE).equals("&1Choose type (Barrel, trapped chest, chest)")) {
      	configFiles.getLang().set(MENU_MAIN_TYPE, "&1Select Chest Item");
      }
      if(configFiles.getLang().getString("Menu.main.content").equals("&1Chest content editing")) {
        	configFiles.getLang().set("Menu.main.content", "&1Edit Chest Contents");
        }
      if(configFiles.getLang().getString("Menu.main.chances").equals("&1Items chances editing")) {
      	configFiles.getLang().set("Menu.main.chances", "&1Edit Item Chances");
      }
      if(configFiles.getLang().getString("Menu.main.particles").equals("&1Particle choosing")) {
        	configFiles.getLang().set("Menu.main.particles", "&1Particle Selection");
      }
      if(configFiles.getLang().getString("Menu.main.copychest").equals("&1Copy settings from another chest")) {
      	configFiles.getLang().set("Menu.main.copychest", "&1Copy Chest");
      }
      if(configFiles.getLang().getString("Menu.time.name").equals("&1Temps de respawn")) {
        	configFiles.getLang().set("Menu.time.name", "&1Respawn Time");
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
