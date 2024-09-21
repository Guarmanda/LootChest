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
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import eu.decentholo.holograms.DecentHologramsPlugin;
import eu.decentholo.holograms.api.DecentHolograms;
import fr.black_eyes.lootchest.colors.Ansi;
import fr.black_eyes.lootchest.colors.Ansi.Attribute;
import fr.black_eyes.lootchest.commands.CommandHandler;
import fr.black_eyes.lootchest.commands.CopyCommand;
import fr.black_eyes.lootchest.commands.CreateCommand;
import fr.black_eyes.lootchest.commands.DespawnAllCommand;
import fr.black_eyes.lootchest.commands.EditCommand;
import fr.black_eyes.lootchest.commands.GetNameCommand;
import fr.black_eyes.lootchest.commands.GiveCommand;
import fr.black_eyes.lootchest.commands.ListCommand;
import fr.black_eyes.lootchest.commands.LocateCommand;
import fr.black_eyes.lootchest.commands.MaxFilledSlotsCommand;
import fr.black_eyes.lootchest.commands.RandomSpawnCommand;
import fr.black_eyes.lootchest.commands.ReloadCommand;
import fr.black_eyes.lootchest.commands.RemoveCommand;
import fr.black_eyes.lootchest.commands.RespawnAllCommand;
import fr.black_eyes.lootchest.commands.RespawnCommand;
import fr.black_eyes.lootchest.commands.SetHoloCommand;
import fr.black_eyes.lootchest.commands.SetPosCommand;
import fr.black_eyes.lootchest.commands.SetProtectionCommand;
import fr.black_eyes.lootchest.commands.SetTimeCommand;
import fr.black_eyes.lootchest.commands.ToggleFallCommand;
import fr.black_eyes.lootchest.commands.TpCommand;
import fr.black_eyes.lootchest.listeners.DeleteListener;
import fr.black_eyes.lootchest.listeners.UiListener;
import fr.black_eyes.lootchest.particles.Particle;
import fr.black_eyes.lootchest.ui.UiHandler;
import lombok.Getter;
import lombok.Setter;







public class Main extends JavaPlugin {
	@Getter private Particle supportedParticles[];
	@Getter private HashMap<Location, Long> protection = new HashMap<>();
	@Getter private HashMap<String, Particle> particles = new HashMap<>();
	@Getter private HashMap<Location, Particle> part = new HashMap<>();
	@Setter public static Config configs;
	@Getter private HashMap<String, Lootchest> lootChest;
	@Getter @Setter private static Main instance;
	@Getter private Files configFiles;
	@Getter private Utils utils;
	@Getter private Boolean useArmorStands;
	@Getter private DecentHologramsPlugin hologramPlugin;
	@Getter private DecentHolograms hologramImpl;
	private static int version = 0;
	Map<String, String> replace = new HashMap<String, String>(){{
		put("&0",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
		put("&1",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
		put("&2",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
		put("&3",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
		put("&4",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
		put("&5",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
		put("&6",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
		put("&7",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
		put("&8",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
		put("&9",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
		put("&a",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
		put("&b",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
		put("&c",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
		put("&d",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
		put("&e",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
		put("&f",Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
		put("&l",Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
		put("&m",Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
		put("&n",Ansi.ansi().a(Attribute.UNDERLINE).toString());
	}};

	
	//the way holograms are working changed a lot since 2.2.4. 
	//If user just done the update, this will be auto set to true by detecting a lacking config option
	//that appeared precisely in 2.2.4
	@Getter private boolean killOldHolograms = false;
	

	@Override
	public void onDisable() {
		hologramPlugin.onDisable();
		Utils.saveAllChests();
		backUp();
		logInfo("&aBacked up data file in case of crash");
	}
	
	/**
	 * Send a message to logs with colors, only if logs are enabled in config.
	 * @param msg the message to send
	 */
    public void logInfo(String msg) {
    	if(configFiles.getConfig() ==null || !configFiles.getConfig().isSet("ConsoleMessages") || configFiles.getConfig().getBoolean("ConsoleMessages")) {
			// use replace to replace all the keys from the map with their values
			for (Map.Entry<String, String> entry : replace.entrySet()) {
				msg = msg.replace(entry.getKey(), entry.getValue().toString());
			}
			//add reset to the end of the message
			msg = msg + Ansi.ansi().a(Attribute.RESET).toString();
			Bukkit.getLogger().info("[LootChest] "+msg);
		}
	}
    
    /**
     * Check if bungee is enabled in spigot config
     * @return true if bungee is enabed, else false
     */
    private boolean hasBungee(){
        boolean bungee = SpigotConfig.bungee;
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        return (bungee && !onlineMode);
    }
	
	/**
	 * Returns the version of your server (the x in 1.x)
	 * For versions >= 1.20.6, it returns for example 126 for 1.20.6, 
	 * because many things changed in 1.20.6, and that's the first minor 
	 * version that has so much impacts compared to its major version
	 * 
	 * @return The version number
	 */
	public static int getVersion() {
		if(version == 0) {
			String complete_ver = Bukkit.getBukkitVersion().split("-")[0];
			int first_digits = Integer.parseInt(complete_ver.split("[.]")[1]);
			int second_digits = -1;
			if(complete_ver.split("[.]").length>2)
				second_digits = Integer.parseInt(complete_ver.split("[.]")[2]);
			if(second_digits != -1 && (first_digits > 20  || (first_digits == 20 && second_digits >=6 ))){
				version = first_digits * 10 + second_digits;
			}else if (first_digits > 20){
				version = first_digits * 10;
			}
			else{
				version = first_digits;
			}
		}
		return version;
	}
    
	@Override
	public void onEnable() {
		setInstance(this);
		if(hologramPlugin == null && getVersion() > 7){
			hologramPlugin = new DecentHologramsPlugin();
		}
		if (getVersion() > 7){
			hologramPlugin.onLoad(this);
			hologramImpl = hologramPlugin.onEnable();
		}

		int pluginId = 21246; // <-- Replace with the id of your plugin!
       	new Metrics(this, pluginId);
		
		configFiles = new Files();
		lootChest = new HashMap<>();
		utils = new Utils();
		useArmorStands = true;
		//initialisation des matériaux dans toutes les verions du jeu
        //initializing materials in all game versions, to allow cross-version compatibility
        Mat.init_materials();
		logInfo("Server version: 1." + getVersion() );
		logInfo("Loading config files...");
		if(!configFiles.initFiles()) {
        	logInfo("&cConfig or data files couldn't be initialized, the plugin will stop.");
        	return;
        }
		
		UiHandler uiHandler = new UiHandler(this);
		registerEvents(uiHandler);
		registerCommands(uiHandler);
        super.onEnable();
        
        //In many versions, I add some text an config option. These lines are done to update config and language files without erasing options that are already set
        updateOldConfig();
        configFiles.saveConfig();
        configFiles.saveLang();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeChannel());


        //If we're on paperspigot, in 1.9+, we check if armorstands ticks aren't disabled. If they are, we use 
        //falling blocks insteand of armor stands for fall effect
        if(getVersion()>8 ) {
        	//Paper has many forks so instead of checking if we're on paper/fork or not, let's just try and see if we can get the
        	//settings.
        	try {
	        	if(org.bukkit.Bukkit.getServer().spigot().getPaperConfig().isSet("world-settings.default.armor-stands-tick")
		        	&& !org.bukkit.Bukkit.getServer().spigot().getPaperConfig().getBoolean("world-settings.default.armor-stands-tick")) {
		        		useArmorStands = false;
		        		logInfo("&eYou disabled 'armor-stands-tick' in paper.yml. ArmorStands will not have gravity, so fall effect will use falling blocks instead! Some blocks can't be used as falling blocks. If so, only fireworks will show!");
		        		logInfo("&eIf no blocks are spawned with the fireworks, use another type of block for fall-effect in config.yml or enable 'armor-stands-tick' in paper.yml");
		        	
	        	}
        	}catch(NoSuchMethodError e) {}
        }
        
		
		//load config
		setConfigs(Config.getInstance(configFiles.getConfig()));

		//If we enabled bungee broadcast but we aren't on a bungee server, not any message will show
        if(!hasBungee() && configs.NOTE_bungee_broadcast) {
    		logInfo("&cYou enaled bungee broadcast in config but you didn't enable bungeecord in spigot config!");
    		logInfo("&cSo if this server isn't in a bungee network, no messages will be sent at all on chest spawn!");
        }
 
        if( !useArmorStands && Main.configs.FALL_Block.equals("CHEST")) {
        	configFiles.getConfig().set("Fall_Effect.Block", "NOTE_BLOCK");
        	configs.FALL_Block = "NOTE_BLOCK";
        }
        

        if(configs.CheckForUpdates) {
        	logInfo("Checking for update...");
        	 new Updater(this);
        }

		//if 1.7, disable world border check
		if(getVersion()<=7) {
			configs.UseHologram = false;
			configs.WorldBorder_Check_For_Spawn = false;
			logInfo("&eYou're using 1.7 or below, I disabled worldborder check because worldborder is implemented in spigot from 1.8");
			logInfo("&eYou're using 1.7 or below, I disabled holograms because it uses armorstands, which are implemented in spigot from 1.8");
					
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
			if(Main.getVersion() > 7){
				new BukkitRunnable() {
					public void run() {
						try{
							float radius = (float) configs.PART_radius;
							float speed = (float)configs.PART_speed;
							int number = configs.PART_number;
							if (configs.PART_enable) {
								for(Map.Entry<Location, Particle> entry: part.entrySet()) {
									Boolean loaded = entry.getKey().getWorld().isChunkLoaded((int)entry.getKey().getX()/16, (int)entry.getKey().getZ()/16) ;
									if (loaded) {	
										if(entry.getValue()!=null)
											try{
												entry.getValue().display(radius, radius, radius, speed, number, entry.getKey(), entry.getKey().getWorld().getPlayers());
											}catch(Exception e) {
												// concurrent modification exception, just ignore it
											}
									}
								}
							}
						}catch(Exception e) {
							// concurrent modification exception, just ignore it
						}
					}
				}.runTaskTimer(this, 0, configs.PART_respawn_ticks);
			}else{
				new BukkitRunnable() {
					public void run() {
						float radius = (float) configs.PART_radius;
						float speed = (float)configs.PART_speed;
						int number = configs.PART_number;
						if (configs.PART_enable) {
							for(Map.Entry<Location, Particle> entry: part.entrySet()) {
								Boolean loaded = entry.getKey().getWorld().isChunkLoaded((int)entry.getKey().getX()/16, (int)entry.getKey().getZ()/16) ;
								if (loaded && entry.getValue()!=null)
									entry.getValue().display(radius, radius, radius, speed, number, entry.getKey(), entry.getKey().getWorld().getPlayers());
								
							}
						}
					}
				}.runTaskTimer(this, 0, configs.PART_respawn_ticks);
			}
		}).start();
	}
    		
	/**
	 * Loads all chests asynchronously
	 */
	@SuppressWarnings("deprecation") //compatibility with 1.7
	private void loadChests() {
		long countdown = configs.Cooldown_Before_Plugin_Start;
    	if(countdown>0) 
			logInfo("Chests will load in "+ countdown + " seconds.");
    	
        this.getServer().getScheduler().runTaskLater(this, () -> {
            logInfo("Loading chests...");
            long current = (new Timestamp(System.currentTimeMillis())).getTime();
            for(String keys : configFiles.getData().getConfigurationSection("chests").getKeys(false)) {
                String name = configFiles.getData().getString("chests." + keys + ".position.world");
                String randomName = name;
                if( configFiles.getData().getInt("chests." + keys + ".randomradius")>0) {
                    randomName = configFiles.getData().getString("chests." + keys + ".randomPosition.world");
                }
                if(name != null && Utils.isWorldLoaded(randomName) && Utils.isWorldLoaded(name)) {
                    getLootChest().put(keys, new Lootchest(keys));
                }
                else {
                    logInfo("&cCouldn't load chest "+keys +" : the world " + configFiles.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
                }
            }
            
            logInfo("Loaded "+lootChest.size() + " Lootchests in "+((new Timestamp(System.currentTimeMillis())).getTime()-current) + " miliseconds");
            logInfo("Starting LootChest timers asynchronously...");
            for (final Lootchest lc : lootChest.values()) {
                Bukkit.getScheduler().scheduleAsyncDelayedTask(instance, () ->
                        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                            if (!lc.spawn(false)) {
                                Utils.scheduleReSpawn(lc);
                                lc.reactivateEffects();
                            }
                        }, 0L)
                        , 5L);
            }
            logInfo("Plugin loaded");
                }, countdown+1 * 20);
	}
	
	
  /**
   * In many versions, I add some text an config option. 
   * These lines are done to update config and language files without erasing options that are already set
   */
  private void updateOldConfig() {
	// hotfix
	// in chances.lore, replace all % by nothing
	if(configFiles.getLang().getString("Menu.chances.lore").contains("%")) {
		String lore = configFiles.getLang().getString("Menu.chances.lore");
		lore = lore.replaceAll("%", "");
		configFiles.getLang().set("Menu.chances.lore", lore);
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
      configFiles.setConfig("use_players_locations_for_randomspawn", false);
      configFiles.setConfig("Cooldown_Before_Plugin_Start", 0);
      configFiles.setConfig("Prevent_Chest_Spawn_In_Protected_Places", false);
      configFiles.setConfig("WorldBorder_Check_For_Spawn", true);
	  configFiles.setLang("CantBreakBlockBecauseProtected", "&cYou have to wait [Time] seconds to loot that chest!");
	  configFiles.setLang("editedProtectionTime", "&aYou edited the protection time of chest [Chest]");
      configFiles.setLang("Menu.main.disable_fall", "&aFall effect is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_respawn_natural", "&aNatural-respawn message is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_respawn_cmd", "&aCommand-respawn message is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.disable_take_message", "&aMessage on chest take is enabled. Click to &cDISABLE &ait");
      configFiles.setLang("Menu.main.enable_fall", "&cFall effect is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.enable_respawn_natural", "&cNatural-respawn message is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.enable_respawn_cmd", "&cCommand-respawn message is disabled. Click to &aENABLE &cit");
      configFiles.setLang("Menu.main.type", "&1Select Chest Item");
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
		int index = help.stream().filter(s -> s.contains("removeAllHolo")).findFirst().map(s -> help.indexOf(s)).orElse(-1);
		if(index!=-1) {
			help.remove(index);
		}
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
      }
      
      if(configFiles.getLang().getString("Menu.main.respawnTime").equals("&1Respawn time editing")) {
        	configFiles.getLang().set("Menu.main.respawnTime", "&1Edit Respawn Time");
       }
      if(configFiles.getLang().getString("Menu.main.type").equals("&1Choose type (Barrel, trapped chest, chest)")) {
      	configFiles.getLang().set("Menu.main.type", "&1Select Chest Item");
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
		supportedParticles = new Particle[cpt];
		int i = 0;
		for(Particle p:Particle.values()) {
			if(p.isSupported()) {
				particles.put(p.getName(), p);
				supportedParticles[i++] = p;
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
				Path oldBackup = Paths.get(instance.getDataFolder() +"/backups/"+ (i)+"data.yml");
				try {
					java.nio.file.Files.deleteIfExists(oldBackup);
				} catch (IOException e) {
					e.printStackTrace();
				}
				i+=9;
			}
			i++;
		}
		
		//auto-deletion of backup to keep only the 10 last ones
		Path oldBackup = Paths.get(instance.getDataFolder() +"/backups/"+ (i-10)+"data.yml");
		try {
			java.nio.file.Files.deleteIfExists(oldBackup);
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
