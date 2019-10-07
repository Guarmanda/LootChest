package fr.black_eyes.lootchest.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;


public class Lootchest implements CommandExecutor, TabCompleter  {

	public static HashMap<Player, String> editinv = new HashMap<Player, String>();
	public static HashMap<Player, String> menuName = new HashMap<Player, String>();
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
			Player player= null;
			if(sender instanceof Player) {
				player = (Player)sender;
			}
			if(args.length ==2) {
				switch(args[0]) {
				case "create":
					if (!sender.hasPermission("lootchest.create") && !sender.hasPermission("lootchest.admin") || !(sender instanceof Player)) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.create");
						return false;
					}
					Set<Material> transparent = null;
					Block chest = player.getTargetBlock(transparent, 10);
					if (chest.getType() != Material.CHEST) {
						Utils.msg(sender, "notAChest", " ", " ");
					}
					else if (Utils.isEmpty(((Chest) chest.getState()).getInventory())) {
						Utils.msg(sender, "chestIsEmpy", " ", " ");
					}
					else if (Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestAlreadyExist", "[Chest]", args[1]);
					}
					else {
						Utils.saveChest(chest, args[1]);
						Utils.msg(sender, "chestSuccefulySaved", "[Chest]", args[1]);
						editinv.put(player, args[1]);
						menuName.put(player, Utils.getMsg("Menu.main.name", "[Chest]", args[1]));
						Utils.mainInv(player, args[1]);
					}
					break;
					
					
				case "edit":
					if (!sender.hasPermission("lootchest.edit") && !sender.hasPermission("lootchest.admin") || !(sender instanceof Player)) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.edit");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						editinv.put(player, args[1]);
						menuName.put(player, Utils.getMsg("Menu.main.name", "[Chest]", args[1]));
						Utils.mainInv(player, args[1]);
					}
					break;	
					
				case "remove":
					if (!sender.hasPermission("lootchest.remove") && !sender.hasPermission("lootchest.admin")) 
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.remove");
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					Utils.deleteChest(args[1]);
					Utils.msg(sender, "chestDeleted", "[Chest]", args[1]);
					break;
					
				case "setpos":
					if (!sender.hasPermission("lootchest.setpos") && !sender.hasPermission("lootchest.admin") || !(sender instanceof Player)) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.remove");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					Utils.changepos(args[1], player.getLocation());
					Utils.msg(sender, "changedPosition", "[Chest]", args[1]);
					break;
					
					
				case "respawn":
					if (!sender.hasPermission("lootchest.respawn") && !sender.hasPermission("lootchest.admin")) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.respawn");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						if(Utils.getPosition(args[1]).getWorld() != null) {
							Utils.restoreChest(args[1], true);
							Utils.msg(sender, "succesfulyRespawnedChest", "[Chest]", args[1]);
							if(Main.getInstance().getConfig().getBoolean("respawn_notify.respawn_with_command.enabled") ) {
								Block block = Utils.getPosition(args[1]).getBlock();
								if(Main.getInstance().getData().isSet("chests."+args[1]+".randomradius")) {
									block = Utils.getRandomPosition(args[1]).getBlock();
								}
								String holo = Main.getInstance().getData().getString("chests." + args[1] + ".holo");
								Bukkit.broadcastMessage((((Main.getInstance().getConfig().getString("respawn_notify.respawn_with_command.message").replace("[Chest]", holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+"").replace("&", "§"));
							}
						}
					}
					break;
				
				default:
					for(int i=1; i<=11;i++) {
						Utils.msg(sender, "help.line"+i, "", "");
					}
					
				}
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("respawnall")) {
					if (!sender.hasPermission("lootchest.respawnall") && !sender.hasPermission("lootchest.admin")) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.respawnall");
						return false;
					}
					for(String keys : Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
						if(Utils.getPosition(keys).getWorld() != null) {
							Utils.restoreChest(keys, true);
						}
					}
					if(Main.getInstance().getConfig().getBoolean("respawn_notify.respawn_all_with_command.enabled") ) {
						Bukkit.broadcastMessage(Main.getInstance().getConfig().getString("respawn_notify.respawn_all_with_command.message").replaceAll("&", "§"));
					}
					Utils.msg(sender, "AllChestsReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("lootchest.reload") && !sender.hasPermission("lootchest.admin") ) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.reload");
						return false;
					}
					try {
						Main.getInstance().getConfig().load(Main.getInstance().getConfigF());
						Main.getInstance().getData().load(Main.getInstance().getDataF());
						Main.getInstance().getLang().load(Main.getInstance().getLangF());
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					
					}
	            	for(String keys : Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
	            		if(Utils.getPosition(keys).getWorld() != null) {
	            			Utils.restoreChest(keys, false);
	            		}
	            	}
					Utils.msg(sender, "PluginReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("list")) {
					if (!sender.hasPermission("lootchest.list") && !sender.hasPermission("lootchest.admin")) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.list");
						return false;
					}
					final StringBuilder bc = new StringBuilder();
					for(String keys : Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
						bc.append(" " + String.valueOf(keys));
					}
					Utils.msg(sender, "ListCommand", "[List]", bc.toString());
				}
				else {
					for(int i=1; i<=11;i++) {
						Utils.msg(sender, "help.line"+i, "", "");
					}
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
					if (!sender.hasPermission("lootchest.setholo") && !sender.hasPermission("lootchest.admin") ) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.setholo");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						Main.getInstance().getData().set("chests." + args[1] + ".holo", bc.toString());
						Utils.msg(sender, "hologram_edited", "[Chest]", args[1]);
						Utils.restoreChest(args[1], false);
					}
				}
				else if(args[0].equalsIgnoreCase("settime")) {
					if (!sender.hasPermission("lootchest.settime") && !sender.hasPermission("lootchest.admin")) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.settiime");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					Main.getInstance().getData().set("chests."+args[1]+".time", args[2]);
					Utils.msg(sender, "settime", "[Chest]", args[1]);
				}
				
				
				else if(args[0].equalsIgnoreCase("give")) {
					if (!sender.hasPermission("lootchest.give") && !sender.hasPermission("lootchest.admin")) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.give");
						return false;
					}
					Player arg1 = Bukkit.getPlayerExact(args[1]);
					if (!Main.getInstance().getData().isSet("chests." + args[2] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[2]);
					}
					else if(arg1 == null) {
						Utils.msg(sender, "playerIsNotOnline", "[Player]", args[1]);
					}
					else {
						String msg = Utils.getMsg("giveto", "[Chest]", args[2]);
						sender.sendMessage(msg.replace("[Player]", args[1]));
						msg = Utils.getMsg("givefrom", "[Chest]", args[2]);
						Bukkit.getServer().getPlayer(args[1]).sendMessage(msg.replace("[Player]", sender.getName()));
						Utils.fillInventory(args[2], arg1.getInventory(), false, arg1);
					}
				}
				else if(args[0].equalsIgnoreCase("randomspawn")) {
					if (!sender.hasPermission("lootchest.randomspawn") && !sender.hasPermission("lootchest.admin")) {
						Utils.msg(sender, "noPermission", "[Permission]", "lootchest.randomspawn");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(sender, "chestDoesntExist", "[Chest]", args[1]);
					}
					else if (Integer.parseInt(args[2]) >0) {
						Main.getInstance().getData().set("chests."+args[1]+".randomradius", Integer.parseInt(args[2]));
						Utils.msg(sender, "chestRadiusSet", "[Chest]", args[1]);
						Utils.restoreChest(args[1], true);
					}
				}
				
				
				else {
					for(int i=1; i<=15;i++) {
						Utils.msg(sender, "help.line"+i, "", "");
					}
				}
				
				
			}
			else {
				for(int i=1; i<=15;i++) {
					Utils.msg(sender, "help.line"+i, "", "");
				}
			}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		final String[] completions0 = { "create", "edit", "help", "respawn", "respawnall", "remove", "setholo", "reload", "list", "setpos", "give", "randomspawn"};
		final List<String> chests = new ArrayList<String>();
		for(String g: Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)){
			chests.add(g);
			
		}
		if(args.length == 1){
			return Arrays.asList(completions0);
		}
		else if(args.length ==2) {
			if(args[0].equalsIgnoreCase("randomspawn") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("respawn") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("setholo") || args[0].equalsIgnoreCase("setpos")) {
				return chests;
			}
		}
		return null;
	}
	
}
