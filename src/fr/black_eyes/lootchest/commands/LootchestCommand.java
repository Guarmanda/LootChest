package fr.black_eyes.lootchest.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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
import fr.black_eyes.lootchest.Menu;


public class LootchestCommand extends Menu implements CommandExecutor, TabCompleter  {
    public static int dc;
	public static  HashMap<org.bukkit.entity.Player, String> editinv = new HashMap<org.bukkit.entity.Player, String>();
	public static HashMap<org.bukkit.entity.Player, String> menuName = new HashMap<org.bukkit.entity.Player, String>();
	Files configFiles = Main.getConfigFiles();

	 FileConfiguration data = Main.getConfigFiles().getData();
	 FileConfiguration lang = Main.getConfigFiles().getLang();
	 
	 
/*public Vector perp(Vector onto, Vector u) {
	return u.clone().subtract(proj(onto, u));
}

public Vector proj(Vector onto, Vector u) {
    return onto.clone().multiply(onto.dot(u) / onto.lengthSquared());
}

public void drawInPlane(Player p) {

	    // We will use these for drawing our parametric curve on the plane:
	double twopi = 2 * Math.PI;
	double times = 1 * twopi;
	double division = twopi / 100;
	//This is how far away we want the plane's origin to be:
	double radius = 2;
	
	Location c = p.getEyeLocation();
	Vector nv = c.getDirection().normalize();
	double nx = radius * nv.getX() + c.getX();
	double ny = radius * nv.getY() + c.getY();
	double nz = radius * nv.getZ() + c.getZ();
	Vector ya = perp(nv, new Vector(0, 1, 0)).normalize();
	Vector xa = ya.getCrossProduct(nv).normalize();
	nv.multiply(-1);
	for (double theta = 0; theta < 50; theta += division) {
		double angle = 2 * Math.PI * theta/50;
		 double ax = Math.cos(angle) * radius;
	     double az = Math.sin(angle) * radius;
	    // Coordinates with respect to our basis
	    double xb = ax; //calculate x coordinate
	    double yb = az; //calculate y coordinate
	    double zb = 0;
	    double xi = xa.getX() * xb + ya.getX() * yb + nv.getX() * zb;
	    double yi = xa.getY() * xb + ya.getY() * yb + nv.getY() * zb;
	    double zi = xa.getZ() * xb + ya.getZ() * yb + nv.getZ() * zb;
	 // Translate the coordinates in front of the player
	    double x = xi + nx;
	    double y = yi + ny;
	    double z = zi + nz;
	
	    // Spawn your particle
	        p.spawnParticle(Particle.VILLAGER_HAPPY, new Location(c.getWorld(), x, y, z), 1, 0, 0, 0, 0);
	       
	}

}*/
	 
	 
	 
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
			org.bukkit.entity.Player player= null;
			if(sender instanceof org.bukkit.entity.Player) {
				player = (org.bukkit.entity.Player)sender;
			}
			Lootchest lc = null;
			if (args.length > 0 && !hasPerm(sender, args[0])) {
				return false;
			}
			if (args.length>1 && !Main.getInstance().getLootChest().containsKey(args[1]) && !args[0].equalsIgnoreCase("create")){
				msg(sender, "chestDoesntExist", "[Chest]", args[1]);
				return false;
			}else if(args.length>1 && !args[0].equalsIgnoreCase("create")){
				lc = Main.getInstance().getLootChest().get(args[1]);
			}
			if(args.length ==2) {
				
				switch(args[0]) {


					//drawInPlane(player);
				case "create":
					if(!(sender instanceof org.bukkit.entity.Player)) {
						sender.sendMessage("§cPlease, run this command in-game");
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
					if (chest.getType() != Material.CHEST) {
						msg(sender, "notAChest", " ", " ");
					}
					else if (isEmpty(((Chest) chest.getState()).getInventory())) {
						msg(sender, "chestIsEmpy", " ", " ");
					}
					else if (Main.getInstance().getLootChest().containsKey(args[1])){
						msg(sender, "chestAlreadyExist", "[Chest]", args[1]);
					}
					else {
						Main.getInstance().getLootChest().put(args[1], new Lootchest(chest, args[1]));
						restoreChest(Main.getInstance().getLootChest().get(args[1]), true);
						msg(sender, "chestSuccefulySaved", "[Chest]", args[1]);
						updateData(Main.getInstance().getLootChest().get(args[1]));
						editinv.put(player, args[1]);
						menuName.put(player, getMsg("Menu.main.name", "[Chest]", args[1]));
						mainInv(player, args[1]);
					}
					break;
					
					
				case "edit":
					if(!(sender instanceof org.bukkit.entity.Player)) {
						sender.sendMessage("§cPlease, run this command in-game");
						return false;
					}
					else {
						editinv.put(player, args[1]);
						menuName.put(player, getMsg("Menu.main.name", "[Chest]", args[1]));
						mainInv(player, args[1]);
					}
					break;	
					
				case "remove":
					data.set("chests."+ lc.getName(), null);
					configFiles.saveData();
					deleteChest(lc);
					msg(sender, "chestDeleted", "[Chest]", args[1]);
					break;
				case "togglefall":
					boolean fall = lc.getFall();
					if(fall) {
						lc.setFall(false);
						msg(sender, "disabledFallEffect", "[Chest]", args[1]);
					} else {
						lc.setFall(true);
						msg(sender, "enabledFallEffect", "[Chest]", args[1]);
					}
					updateData(lc);
					break;
				case "setpos":
					changepos(lc, player.getLocation().getBlock().getLocation());
					updateData(lc);
					msg(sender, "changedPosition", "[Chest]", args[1]);
					break;
				case "tp":
					Location loc = lc.getActualLocation();
					player.teleport(loc);
					msg(sender, "teleportedToChest", "[Chest]", args[1]);
					break;
					
					
				case "respawn":

					if(Bukkit.getWorld(lc.getWorld()) != null) {
						restoreChest(lc, true);
						msg(sender, "succesfulyRespawnedChest", "[Chest]", args[1]);
						if(lc.getRespawn_cmd()) {
							Block block = lc.getActualLocation().getBlock();
							String holo = lc.getHolo();
							if(Main.configs.NOTE_bungee_broadcast) {
								BungeeChannel.bungeeBroadcast((((Main.configs.NOTE_command_msg.replace("[Chest]", holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+"").replace("&", "§"));
							}
							else if(!Main.configs.NOTE_per_world_message) {
								for(Player p : Bukkit.getOnlinePlayers()) {
									p.sendMessage((((Main.configs.NOTE_command_msg.replace("[Chest]", holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+"").replace("&", "§"));							
								}
								
							}else {
								for(org.bukkit.entity.Player p : block.getWorld().getPlayers()){
									p.sendMessage((((Main.configs.NOTE_command_msg.replace("[Chest]", holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+"").replace("&", "§"));							
								
								}
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
					msg(sender, "locate_command.main_message", " "," ");
					for(Lootchest lcs : Main.getInstance().getLootChest().values()) {
						if(lcs.getRespawn_natural()) {
							Location block = lcs.getActualLocation();
							String holo = lcs.getHolo();
							sender.sendMessage(getMsg("locate_command.chest_list", "[world]", block.getWorld().getName()).replace("[Chest]", holo).replace("[x]", block.getX()+"").replace("[y]", block.getY()+"").replace("[z]", block.getZ()+""));
						}
					}
				}
				else if(args[0].equalsIgnoreCase("getname")) {
					if(!(sender instanceof org.bukkit.entity.Player)) {
						sender.sendMessage("§cPlease, run this command in-game");
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
				    Lootchest l = isLootChest(chest.getLocation());
				    
					if (chest.getType() != Material.CHEST || l == null) {
						msg(sender, "notAChest", " ", " ");
					}
					else if (l!=null){
						msg(sender, "commandGetName", "[Chest]", l.getName());
					}
				}
				else if(args[0].equalsIgnoreCase("respawnall")) {
		
					for (final Lootchest l : Main.getInstance().getLootChest().values()) {
			            Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
			                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			                            restoreChest(l, true) ;

			                    }, 0L);
			            }, 5L);
			        }
					
					if(Main.configs.NOTE_allcmd_e ) {
						if(Main.configs.NOTE_bungee_broadcast) {
							BungeeChannel.bungeeBroadcast(Main.configs.NOTE_allcmd_msg.replace("&", "§"));
						}else {
							for(Player p : Bukkit.getOnlinePlayers()) {
								p.sendMessage(Main.configs.NOTE_allcmd_msg.replaceAll("&", "§"));
						
							}
						}
					}
					msg(sender, "AllChestsReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("reload")) {
					updateData();
					configFiles.reloadConfig();
					Main.configs = Config.getInstance();
					Main.part.clear();
					Main.getInstance().getLootChest().clear();
					for(String keys : configFiles.getData().getConfigurationSection("chests").getKeys(false)) {
						String name = configFiles.getData().getString("chests." + keys + ".position.world");
						String randomname = name;
						if( configFiles.getData().getInt("chests." + keys + ".randomradius")>0) {
							 randomname = configFiles.getData().getString("chests." + keys + ".randomPosition.world");
						}
						if(name != null && org.bukkit.Bukkit.getWorld(randomname) != null && org.bukkit.Bukkit.getWorld(name) != null) {
							Main.getInstance().getLootChest().put(keys, new Lootchest(keys));
						}
						else {
			    			Main.getInstance().getLogger().info("§cCouldn't load chest "+keys +" : the world " + configFiles.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
						}
			    	}
					
					for (final Lootchest l : Main.getInstance().getLootChest().values()) {
						if(Bukkit.getWorld(l.getWorld()) != null) {
				            Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
				                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
	        							if (!restoreChest(l, false)) {
	        								sheduleRespawn(l);
	        							}
	        							reactivateEffects(l);
	
				                    }, 0L);
				            }, 5L);
						}
					}
					msg(sender, "PluginReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("list")) {
					final StringBuilder bc = new StringBuilder();
					for(String keys : Main.getInstance().getLootChest().keySet()) {
						bc.append(" " + String.valueOf(keys));
					}
					msg(sender, "ListCommand", "[List]", bc.toString());
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
					updateData(lc);
					msg(sender, "hologram_edited", "[Chest]", args[1]);
					restoreChest(lc, false);
					
				}
				else if(args[0].equalsIgnoreCase("settime")) {
					lc.setTime(Integer.parseInt(args[2]));
					updateData(lc);
					restoreChest(lc, true);
					msg(sender, "settime", "[Chest]", args[1]);
				}
				
				
				else if(args[0].equalsIgnoreCase("give")) {
					org.bukkit.entity.Player arg2 = Bukkit.getPlayerExact(args[2]);
					if(arg2 == null) {
						msg(sender, "PlayerIsNotOnline", "[Player]", args[2]);
					}
					else {
						String msg = getMsg("giveto", "[Chest]", args[1]);
						sender.sendMessage(msg.replace("[Player]", args[2]));
						msg = getMsg("givefrom", "[Chest]", args[1]);
						Bukkit.getServer().getPlayer(args[2]).sendMessage(msg.replace("[Player]", sender.getName()));
						fillInventory(lc, arg2.getInventory(), false, arg2);
					}
				}
				else if(args[0].equalsIgnoreCase("randomspawn")) {
					lc.setRadius(Integer.parseInt(args[2]));
					if(Integer.parseInt(args[2]) != 0) {
						msg(sender, "chestRadiusSet", "[Chest]", args[1]);
					}
					
					Location loc;
					if(lc.getRandomPosition()!=null) {
						loc = lc.getRandomPosition();
						if(loc.getBlock().getType().equals(Material.CHEST)) {
							deleteholo(loc);
							((Chest) loc.getBlock().getState()).getInventory().clear();
							loc.getBlock().setType(Material.AIR);
							loc.add(0.5,0.5,0.5);
							Main.part.remove(loc);
						}
					}
					loc = lc.getPosition();
					if(loc.getBlock().getType().equals(Material.CHEST)) {
						deleteholo(loc);
						((Chest) loc.getBlock().getState()).getInventory().clear();
						loc.getBlock().setType(Material.AIR);
						loc.add(0.5,0.5,0.5);
						Main.part.remove(loc);
					}
					if(Integer.parseInt(args[2]) == 0) {
						lc.setRandomLoc(null);
						msg(sender, "disabledChestRadius", "[Chest]", args[1]);
					}
					restoreChest(lc, true);
					
					updateData(lc);
						
					

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
			p.sendMessage(help.get(i).replace("&", "§"));
		}
	}

	boolean hasPerm(CommandSender sender, String permission) {
		if (!sender.hasPermission("lootchest." + permission) && !sender.hasPermission("lootchest.admin") && !sender.hasPermission("lootchest.*")) {
			msg(sender, "noPermission", "[Permission]", "lootchest." + permission);
			return false;
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		final String[] completions0 = { "locate", "create", "edit", "help", "respawn", "respawnall", "remove", "setholo", "reload", "list", "setpos", "give", "randomspawn", "tp", "settime","togglefall", "getname"};
		final List<String> chests = new ArrayList<String>();
		for(String g: data.getConfigurationSection("chests").getKeys(false)){
			chests.add(g);
			
		}
		/*completion improved by alessevan*/
		if(args.length == 1){
		    final List<String> completions = new ArrayList<>();
		    for(final String string : completions0){
		        if(string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
		    }
		    return completions;
		}
		else if(args.length ==2) {
			if(args[0].equalsIgnoreCase("randomspawn") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("respawn") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("setholo") || args[0].equalsIgnoreCase("setpos")|| args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("give")|| args[0].equalsIgnoreCase("settime") || args[0].equalsIgnoreCase("togglefall") ) {
				return chests;
			}
		}
		return null;
	}
	
}
