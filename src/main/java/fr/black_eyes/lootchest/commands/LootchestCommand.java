package fr.black_eyes.lootchest.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import fr.black_eyes.lootchest.BungeeChannel;
import fr.black_eyes.lootchest.Config;
import fr.black_eyes.lootchest.Files;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Menu;
import fr.black_eyes.lootchest.Utils;


public class LootchestCommand implements CommandExecutor, TabCompleter  {
	
    public static int dc;
	public static  Map<org.bukkit.entity.Player, String> editinv = new HashMap<>();
	public static Map<org.bukkit.entity.Player, String> menuName = new HashMap<>();
	private Files configFiles;
	
	private FileConfiguration data; 
	 private Menu menu;
	 private Utils utils;
	 private Main main;
	 
	//variables for command completion
	private static final String[] completions0 = {"removeallholo", "locate", "create", "edit", "help", "respawn", "respawnall", "remove", "setholo", "setprotection", "reload", "list", "setpos", "give", "randomspawn", "tp", "settime","togglefall", "getname"};
	
	//following args must be followed by chest names
	private static final List<String> argsFollowedByChest = new ArrayList<>(
			Arrays.asList("copy","randomspawn", "edit", "respawn", "remove", "setholo", "setpos", "tp", "give", "settime", "togglefall", "setprotection")
			);
		private static final List<String> args2FollowedByChest = new ArrayList<>(
			Arrays.asList("copy")
			);
		
	 
	 public LootchestCommand() {
		 	main = Main.getInstance();

			configFiles = main.getConfigFiles();
			data = configFiles.getData();
			menu = main.getMenu();
			utils = main.getUtils();
	 }
	 
	@Override
	@SuppressWarnings("deprecation") //compatibility with 1.7
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
			String cheststr = "[Chest]";
			String runCmdInGame = Utils.color("&cPlease, run this command in-game");
			org.bukkit.entity.Player player= null;
			if(sender instanceof org.bukkit.entity.Player) {
				player = (org.bukkit.entity.Player)sender;
			}
			Lootchest lc = null;
			if (args.length > 0 && !hasPerm(sender, args[0])) {
				return false;
			}
			if (args.length>1 && !Main.getInstance().getLootChest().containsKey(args[1]) && !args[0].equalsIgnoreCase("create")){
				Utils.msg(sender, "chestDoesntExist", cheststr, args[1]);
				return false;
			}else if(args.length>1 && !args[0].equalsIgnoreCase("create")){
				lc = Main.getInstance().getLootChest().get(args[1]);
			}
			if(args.length ==2) {
				
				switch(args[0]) {

				case "create":
					if(!(sender instanceof org.bukkit.entity.Player)) {
						sender.sendMessage(runCmdInGame);
						return false;
					}
					Block chest;
					BlockIterator iter = new BlockIterator(player, 10);
				    Block lastBlock = iter.next();
				    while (iter.hasNext()) {
				        lastBlock = iter.next();
				        if (lastBlock.getType() == Material.AIR) continue;
				        break;
				    }
				    chest = lastBlock;
					if (!Mat.isALootChestBlock(chest)) {
						Utils.msg(sender, "notAChest", " ", " ");
					}
					else if (Utils.isEmpty(((InventoryHolder) chest.getState()).getInventory())) {
						Utils.msg(sender, "chestIsEmpy", " ", " ");
					}
					else if (Main.getInstance().getLootChest().containsKey(args[1])){
						Utils.msg(sender, "chestAlreadyExist", cheststr, args[1]);
					}
					else {
						Main.getInstance().getLootChest().put(args[1], new Lootchest(chest, args[1]));
						Main.getInstance().getLootChest().get(args[1]).spawn( true);
						Utils.msg(sender, "chestSuccefulySaved", cheststr, args[1]);
						utils.updateData(Main.getInstance().getLootChest().get(args[1]));
						editinv.put(player, args[1]);
						menuName.put(player, Utils.getMsg("Menu.main.name", cheststr, args[1]));
						menu.mainInv(player, args[1]);
					}
					break;
					
					
				case "edit":
					if(!(sender instanceof org.bukkit.entity.Player)) {
						sender.sendMessage(runCmdInGame);
						return false;
					}
					else {
						editinv.put(player, args[1]);
						menuName.put(player, Utils.getMsg("Menu.main.name", cheststr, args[1]));
						menu.mainInv(player, args[1]);
					}
					break;	
					
				case "remove":
					data.set("chests."+ lc.getName(), null);
					configFiles.saveData();
					Utils.deleteChest(lc);
					Utils.msg(sender, "chestDeleted", cheststr, args[1]);
					break;
				case "togglefall":
					boolean fall = lc.getFall();
					if(fall) {
						lc.setFall(false);
						Utils.msg(sender, "disabledFallEffect", cheststr, args[1]);
					} else {
						lc.setFall(true);
						Utils.msg(sender, "enabledFallEffect", cheststr, args[1]);
					}
					utils.updateData(lc);
					break;
				case "setpos":
					lc.setDirection(getCardinalDirection(player));
					utils.changepos(lc, player.getLocation().getBlock().getLocation());
					utils.updateData(lc);
					Utils.msg(sender, "changedPosition", cheststr, args[1]);
					break;
				case "tp":
					Location loc = lc.getActualLocation();
					player.teleport(loc);
					Utils.msg(sender, "teleportedToChest", cheststr, args[1]);
					break;
					
					
				case "respawn":
					lc.spawn( true);
					Utils.msg(sender, "succesfulyRespawnedChest", cheststr, args[1]);
					if(lc.getRespawn_cmd()) {
						Block block = lc.getActualLocation().getBlock();
						String holo = lc.getHolo();
						String message = Utils.color((((Main.configs.NOTE_command_msg.replace("[World]", block.getWorld().getName()).replace(cheststr, holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+""));
						if(Main.configs.NOTE_bungee_broadcast) {
							BungeeChannel.bungeeBroadcast(message);
						}
						else if(!Main.configs.NOTE_per_world_message) {
							for(World w : Bukkit.getWorlds()) {
								for(Player p : w.getPlayers()) {
									Utils.sendMultilineMessage(message, p);
								}
							}
							
						}else {
							for(org.bukkit.entity.Player p : block.getWorld().getPlayers()){
								Utils.sendMultilineMessage(message, p);							
							}
						}
					}
					
					
					break;
				


				default:
					displayhelp(sender);
					
				}
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("locate")) {
					Utils.msg(sender, "locate_command.main_message", " "," ");
					for(Lootchest lcs : Main.getInstance().getLootChest().values()) {
						if(lcs.getRespawn_natural() && !lcs.getTaken()) {
							Location block = lcs.getActualLocation();
							String holo = lcs.getHolo();
							Utils.msg(sender, "locate_command.chest_list", "[world]", block.getWorld().getName(), cheststr, holo, "[x]", block.getX()+"", "[y]", block.getY()+"", "[z]", block.getZ()+"");
						}
					}
				}
				else if(args[0].equalsIgnoreCase("getname")) {
					if(!(sender instanceof org.bukkit.entity.Player)) {
						sender.sendMessage(runCmdInGame);
						return false;
					}
					Block chest;
					BlockIterator iter = new BlockIterator(player, 10);
				    Block lastBlock = iter.next();
				    while (iter.hasNext()) {
				        lastBlock = iter.next();
				        if (lastBlock.getType() == Material.AIR) {
				            continue;
				        }
				        break;
				    }
				    chest = lastBlock;
				    Lootchest l = Utils.isLootChest(chest.getLocation());
				    
					if (l == null || !l.isGoodType(chest)) {
						Utils.msg(sender, "notAChest", " ", " ");
					}
					else if (l!=null){
						Utils.msg(sender, "commandGetName", cheststr, l.getName());
					}
				}
				else if(args[0].equalsIgnoreCase("removeallholo")) {
					int cpt = utils.killOldHolograms(false);
					Utils.msg(sender, "removedHolograms", "[Number]", cpt +"");
				}
				else if(args[0].equalsIgnoreCase("respawnall")) {
		
					for (final Lootchest l : Main.getInstance().getLootChest().values()) {
						Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
									l.spawn( true) ;

								}, 0L);
						}, 5L);
			        }
					
					if(Main.configs.NOTE_allcmd_e ) {
						if(Main.configs.NOTE_bungee_broadcast) {
							BungeeChannel.bungeeBroadcast(Utils.color(Main.configs.NOTE_allcmd_msg));
						}else {
							for(World w : Bukkit.getWorlds()) {
								for(Player p : w.getPlayers()) {
									Utils.sendMultilineMessage(Main.configs.NOTE_allcmd_msg, p);
								}
							}
						}
					}
					Utils.msg(sender, "AllChestsReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("reload")) {
					if(Config.getInstance().saveDataFileDuringReload) {
						utils.updateData();
					}else{
						configFiles.reloadData();
					}
					configFiles.reloadConfig();
					Main.configs = Config.getInstance(configFiles.getConfig());
					main.getPart().clear();
					if(!Bukkit.getVersion().contains("1.7")) {
						Main.getInstance().getLootChest().values().stream().forEach(chest -> chest.getHologram().remove());
					}
					Main.getInstance().getLootChest().clear();
					for(String keys : configFiles.getData().getConfigurationSection("chests").getKeys(false)) {
						String name = configFiles.getData().getString("chests." + keys + ".position.world");
						String randomname = name;
						if( configFiles.getData().getInt("chests." + keys + ".randomradius")>0) {
							 randomname = configFiles.getData().getString("chests." + keys + ".randomPosition.world");
						}
						if(name != null && Utils.isWorldLoaded(randomname) && Utils.isWorldLoaded(name)) {
							Main.getInstance().getLootChest().put(keys, new Lootchest(keys));
						}
						else {
			    			Main.getInstance().logInfo("&cCouldn't load chest "+keys +" : the world " + configFiles.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
						}
			    	}
					
					for (final Lootchest l : Main.getInstance().getLootChest().values()) {
						if(Utils.isWorldLoaded(l.getWorld())) {
							Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
										if (!l.spawn( false)) {
											Utils.sheduleRespawn(l);
											utils.reactivateEffects(l);
										}
										

									}, 0L);
							}, 5L);
						}
					}
					Utils.msg(sender, "PluginReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("list")) {
					final StringBuilder bc = new StringBuilder();
					for(String keys : Main.getInstance().getLootChest().keySet()) {
						bc.append(" " + String.valueOf(keys));
					}
					Utils.msg(sender, "ListCommand", "[List]", bc.toString());
				}
				else {
					displayhelp(sender);
				}
			}
			else if(args.length >= 3) {
				if(args[0].equalsIgnoreCase("setholo") && Main.getInstance().getConfig().getBoolean("UseHologram")) {
					final StringBuilder bc = new StringBuilder();
					bc.append(String.valueOf(args[2]));
					if(args.length > 3) {
						for (int i = 3; i<args.length; i++) {
							bc.append(" " + String.valueOf(args[i]));
						}
						
                    }
					lc.setHolo(bc.toString());
					utils.updateData(lc);
					Utils.msg(sender, "hologram_edited", cheststr, args[1]);
					lc.spawn(false);
					
				}
				else if(args[0].equalsIgnoreCase("settime")) {
					lc.setTime(Integer.parseInt(args[2]));
					utils.updateData(lc);
					lc.spawn( true);
					Utils.msg(sender, "settime", cheststr, args[1]);
				}
				
				else if(args[0].equalsIgnoreCase("setprotection")){
					lc.setProtectionTime(Integer.parseInt(args[2]));
					utils.updateData(lc);
					Utils.msg(sender, "editedProtectionTime", cheststr, args[1]);
				}
				
				
				else if(args[0].equalsIgnoreCase("give")) {
					org.bukkit.entity.Player arg2 = Bukkit.getPlayerExact(args[2]);
					if(arg2 == null) {
						Utils.msg(sender, "PlayerIsNotOnline", "[Player]", args[2]);
					}
					else {
						Utils.msg(sender, "giveto", cheststr, args[1], "[Player]", args[2]);
						Utils.msg(Bukkit.getServer().getPlayer(args[2]), "givefrom", cheststr, args[1], "[Player]", sender.getName());
						Utils.fillInventory(lc, arg2.getInventory(), false, arg2);
					}
				}
				else if(args[0].equalsIgnoreCase("randomspawn")) {
					Integer newradius = Integer.parseInt(args[2]);
					newradius = newradius>0?newradius:0;
					lc.setRadius(newradius);
					if(newradius> 0) {
						Utils.msg(sender, "chestRadiusSet", cheststr, args[1]);
					}
					
					lc.despawn();
					if(newradius == 0) {
						lc.setRandomLoc(null);
						Utils.msg(sender, "disabledChestRadius", cheststr, args[1]);
					}
					lc.spawn(true);
					
					utils.updateData(lc);
						
					

				}else if (args[0].equalsIgnoreCase("copy")){
					// check if third arg is a chest
					if (!Main.getInstance().getLootChest().containsKey(args[2]) ){
						Utils.msg(sender, "chestDoesntExist", cheststr, args[2]);
						return false;
					}
					utils.copychest(Main.getInstance().getLootChest().get(args[2]), lc);
					Utils.msg(sender, "copiedChest", "[Chest1]", Main.getInstance().getLootChest().get(args[2]).getName(), "[Chest2]", lc.getName());

				}
				
				
				else {
					displayhelp(sender);
				}
				
				
			}
			else {
				displayhelp(sender);
			}
		
		return false;
	}
	
	public void displayhelp(CommandSender p) {
		List<String> help = configFiles.getLang().getStringList("help");
		for(int i=0; i<help.size();i++) {
			p.sendMessage(Utils.color(help.get(i)));
		}
	}
	
	public static String getCardinalDirection(Player player) {
		float rotation = Utils.normalizeYaw(player.getLocation().getYaw());
        if (rotation>135.0 || rotation <= -135.0) {
            return "NORTH";
        } else if ( rotation > -135.0 && rotation < -45.0) {
            return "EAST";
        } else if ( rotation >= -45.0 && rotation < 45.0) {
            return "SOUTH";
        }  else if (rotation >= 45.0 && rotation <= 135.0) {
            return "WEST";
        }
		return null;
    }

	boolean hasPerm(CommandSender sender, String permission) {
		if (!sender.hasPermission("lootchest." + permission) && !sender.hasPermission("lootchest.admin") && !sender.hasPermission("lootchest.*")) {
			Utils.msg(sender, "noPermission", "[Permission]", "lootchest." + permission);
			return false;
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		final List<String> chests = new ArrayList<>(Main.getInstance().getLootChest().keySet());
		
		/*completion improved by alessevan*/
		if(args.length == 1){
		    final List<String> completions = new ArrayList<>();
		    for(String string : completions0){
		        if(string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
		    }
		    return completions;
		}

		else if(args.length ==2 && argsFollowedByChest.contains(args[0].toLowerCase())) {
			    final List<String> completions = new ArrayList<>();
			    for(String string : chests){
			        if(string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
			    }
			    return completions;
			
		}
		else if(args.length ==3 && args2FollowedByChest.contains(args[0].toLowerCase())) {
			final List<String> completions = new ArrayList<>();
			for(String string : chests){
				if(string.toLowerCase().startsWith(args[2].toLowerCase())) completions.add(string);
			}
			return completions;
		
		}
		return null;
	}
	
}
