package fr.black_eyes.lootchest.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
import fr.black_eyes.lootchest.Config;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;


public class LootchestCommand extends Utils implements CommandExecutor, TabCompleter  {

	public static  HashMap<Player, String> editinv = new HashMap<Player, String>();
	public static HashMap<Player, String> menuName = new HashMap<Player, String>();
	Config config = Main.getConfigFiles();
	 FileConfiguration data = Main.getConfigFiles().getData();
	 FileConfiguration lang = Main.getConfigFiles().getLang();
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
			Player player= null;
			if(sender instanceof Player) {
				player = (Player)sender;
			}
			if(args.length ==2) {
				switch(args[0]) {

				case "create":
					if (!hasPerm(sender, "create")) {
						return false;
					}
					else if(!(sender instanceof Player)) {
						sender.sendMessage("§cPlease, run this command in-game");
						return false;
					}
					
					Set<Material> transparent = null;
					Block chest = player.getTargetBlock(transparent, 10);
					if (chest.getType() != Material.CHEST) {
						msg(sender, "notAChest", " ", " ");
					}
					else if (isEmpty(((Chest) chest.getState()).getInventory())) {
						msg(sender, "chestIsEmpy", " ", " ");
					}
					else if (data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestAlreadyExist", "[Chest]", args[1]);
					}
					else {
						saveChest(chest, args[1]);
						msg(sender, "chestSuccefulySaved", "[Chest]", args[1]);
						editinv.put(player, args[1]);
						menuName.put(player, getMsg("Menu.main.name", "[Chest]", args[1]));
						mainInv(player, args[1]);
					}
					break;
					
					
				case "edit":
					if (!hasPerm(sender, "edit")) {
						return false;
					}
					else if(!(sender instanceof Player)) {
						sender.sendMessage("§cPlease, run this command in-game");
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						editinv.put(player, args[1]);
						menuName.put(player, getMsg("Menu.main.name", "[Chest]", args[1]));
						mainInv(player, args[1]);
					}
					break;	
					
				case "remove":
					if (!hasPerm(sender, "remove")) {
						return false;
					}
					if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					deleteChest(args[1]);
					msg(sender, "chestDeleted", "[Chest]", args[1]);
					break;
				case "togglefall":
					if (!sender.hasPermission("lootchest.togglefall") && !sender.hasPermission("lootchest.admin") || !(sender instanceof Player)) {
						msg(sender, "noPermission", "[Permission]", "lootchest.togglefall");
						return false;
					}
					boolean fall = data.getBoolean("chests." + args[1] +".fall");
					if(fall) {
						data.set("chests." + args[1] + ".fall", false);
						msg(sender, "disabledFallEffect", "[Chest]", args[1]);
					} else {
						data.set("chests." + args[1] + ".fall", true);
						msg(sender, "enabledFallEffect", "[Chest]", args[1]);
					}
					break;
				case "setpos":
					if (!hasPerm(sender, "setpos")) {
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					changepos(args[1], player.getLocation());
					msg(sender, "changedPosition", "[Chest]", args[1]);
					break;
				case "tp":
					if (!sender.hasPermission("lootchest.tp") && !sender.hasPermission("lootchest.admin") || !(sender instanceof Player)) {
						msg(sender, "noPermission", "[Permission]", "lootchest.tp");
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					Location loc = getPosition(args[1]);
					if(getRandomPosition(args[1]) != null) {
						loc = getRandomPosition(args[1]);
					}
					player.teleport(loc);
					msg(sender, "teleportedToChest", "[Chest]", args[1]);
					break;
					
					
				case "respawn":
					if (!hasPerm(sender, "respawn")) {
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						if(getPosition(args[1]).getWorld() != null) {
							restoreChest(args[1], true);
							msg(sender, "succesfulyRespawnedChest", "[Chest]", args[1]);
							if(Main.getInstance().getConfig().getBoolean("respawn_notify.respawn_with_command.enabled") ) {
								Block block = getPosition(args[1]).getBlock();
								if(data.isSet("chests."+args[1]+".randomradius")) {
									block = getRandomPosition(args[1]).getBlock();
								}
								String holo = data.getString("chests." + args[1] + ".holo");
								if(!Main.getInstance().getConfig().getBoolean("respawn_notify.per_world_message")) {
									Bukkit.broadcastMessage((((Main.getInstance().getConfig().getString("respawn_notify.respawn_with_command.message").replace("[Chest]", holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+"").replace("&", "§"));							
								}else {
									for(Player p : block.getWorld().getPlayers()){
										p.sendMessage((((Main.getInstance().getConfig().getString("respawn_notify.respawn_with_command.message").replace("[Chest]", holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+"").replace("&", "§"));							
										
									}
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

				if(args[0].equalsIgnoreCase("respawnall")) {
					if (!hasPerm(sender, "respawnall")) {
						return false;
					}
					for(String keys : data.getConfigurationSection("chests").getKeys(false)) {
						if(getPosition(keys).getWorld() != null) {
							restoreChest(keys, true);
						}
					}
					if(Main.getInstance().getConfig().getBoolean("respawn_notify.respawn_all_with_command.enabled") ) {
						Bukkit.broadcastMessage(Main.getInstance().getConfig().getString("respawn_notify.respawn_all_with_command.message").replaceAll("&", "§"));
					}
					msg(sender, "AllChestsReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("reload")) {
					if (!hasPerm(sender, "reload")) {
						return false;
					}
					config.reloadConfig();
	            	for(String keys : data.getConfigurationSection("chests").getKeys(false)) {
	            		if(getPosition(keys).getWorld() != null) {
	            			restoreChest(keys, false);
	            		}
	            	}
					msg(sender, "PluginReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("list")) {
					if (!hasPerm(sender, "list")) {
						return false;
					}
					final StringBuilder bc = new StringBuilder();
					for(String keys : data.getConfigurationSection("chests").getKeys(false)) {
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
					if (!hasPerm(sender, "setholo")) {
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						data.set("chests." + args[1] + ".holo", bc.toString());
						msg(sender, "hologram_edited", "[Chest]", args[1]);
						restoreChest(args[1], false);
					}
				}
				else if(args[0].equalsIgnoreCase("settime")) {
					if (!hasPerm(sender, "settime")) {
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					data.set("chests."+args[1]+".time", args[2]);
					msg(sender, "settime", "[Chest]", args[1]);
				}
				
				
				else if(args[0].equalsIgnoreCase("give")) {
					Player arg1 = Bukkit.getPlayerExact(args[1]);
					if (!hasPerm(sender, "give")) {
						return false;
					}
					
					else if (!data.isSet("chests." + args[2] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[2]);
					}
					else if(arg1 == null) {
						msg(sender, "playerIsNotOnline", "[Player]", args[1]);
					}
					else {
						String msg = getMsg("giveto", "[Chest]", args[2]);
						sender.sendMessage(msg.replace("[Player]", args[1]));
						msg = getMsg("givefrom", "[Chest]", args[2]);
						Bukkit.getServer().getPlayer(args[1]).sendMessage(msg.replace("[Player]", sender.getName()));
						fillInventory(args[2], arg1.getInventory(), false, arg1);
					}
				}
				else if(args[0].equalsIgnoreCase("randomspawn")) {
					if (!hasPerm(sender, "randomspawn")) {
						return false;
					}
					else if (!data.isSet("chests." + args[1] + ".time")){
						msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else if (Integer.parseInt(args[2]) >0) {
						data.set("chests."+args[1]+".randomradius", Integer.parseInt(args[2]));
						msg(sender, "chestRadiusSet", "[Chest]", args[1]);
						restoreChest(args[1], true);
					}
					else if(Integer.parseInt(args[2]) == 0) {
						
						msg(sender, "disabledChestRadius", "[Chest]", args[1]);
						restoreChest(args[1], true);
						data.set("chests."+args[1]+".randomradius", null);
						data.set("chests."+args[1]+".randomPosition", null);
					}
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
		for(int i=1; i<=17;i++) {
			msg(p, "help.line"+i, "", "");
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
		final String[] completions0 = { "create", "edit", "help", "respawn", "respawnall", "remove", "setholo", "reload", "list", "setpos", "give", "randomspawn", "tp", "settime"};
		final List<String> chests = new ArrayList<String>();
		for(String g: data.getConfigurationSection("chests").getKeys(false)){
			chests.add(g);
			
		}
		if(args.length == 1){
			return Arrays.asList(completions0);
		}
		else if(args.length ==2) {
			if(args[0].equalsIgnoreCase("randomspawn") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("respawn") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("setholo") || args[0].equalsIgnoreCase("setpos")|| args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("give")|| args[0].equalsIgnoreCase("settime") || args[0].equalsIgnoreCase("togglefall") ) {
				return chests;
			}
		}
		return null;
	}
	
}
