package fr.black_eyes.lootchest.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;

public class Lootchest implements CommandExecutor  {

	public static HashMap<Player, String> editinv = new HashMap<Player, String>();
	
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args.length ==2) {
				switch(args[0]) {
				case "create":
					if (!player.hasPermission("lootchest.create") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.create");
						return false;
					}
					Set<Material> transparent = null;
					Block chest = player.getTargetBlock(transparent, 10);
					if (chest.getType() != Material.CHEST) {
						Utils.msg(player, "notAChest", " ", " ");
					}
					else if (Utils.isEmpty(((Chest) chest.getState()).getInventory())) {
						Utils.msg(player, "chestIsEmpy", " ", " ");
					}
					else if (Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(player, "chestAlreadyExist", "[Chest]", args[1]);
					}
					else {
						Utils.saveChest(chest, args[1]);
						Utils.msg(player, "chestSuccefulySaved", "[Chest]", args[1]);
						editinv.put(player, args[1]);
						Utils.mainInv(player, args[1]);
					}
					break;
					
					
				case "edit":
					if (!player.hasPermission("lootchest.edit") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.edit");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(player, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						editinv.put(player, args[1]);
						Utils.mainInv(player, args[1]);
					}
					break;	
					
				case "remove":
					if (!player.hasPermission("lootchest.remove") && !player.hasPermission("lootchest.admin")) 
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.remove");
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(player, "chestDoesntExist", "[Chest]", args[1]);
						return false;
					}
					Utils.deleteChest(args[1]);
					Utils.msg(player, "chestDeleted", "[Chest]", args[1]);
					break;
					
					
				case "respawn":
					if (!player.hasPermission("lootchest.respawn") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.respawn");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(player, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						if(Utils.getPosition(args[1]).getWorld() != null) {
							Utils.restoreChest(args[1], true);
							Utils.msg(player, "succesfulyRespawnedChest", "[Chest]", args[1]);
						}
					}
					break;
				default:
					for(int i=1; i<=11;i++) {
						Utils.msg(player, "help.line"+i, "", "");
					}
					
				}
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("respawnall")) {
					if (!player.hasPermission("lootchest.respawnall") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.respawnall");
						return false;
					}
					for(String keys : Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
						if(Utils.getPosition(keys).getWorld() != null) {
							Utils.restoreChest(keys, true);
						}
					}
					Utils.msg(player, "AllChestsReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("reload")) {
					if (!player.hasPermission("lootchest.reload") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.reload");
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
					Utils.msg(player, "PluginReloaded", " ", " ");
				}
				else if(args[0].equalsIgnoreCase("list")) {
					if (!player.hasPermission("lootchest.list") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.list");
						return false;
					}
					final StringBuilder bc = new StringBuilder();
					for(String keys : Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
						bc.append(" " + String.valueOf(keys));
					}
					Utils.msg(player, "ListCommand", "[List]", bc.toString());
				}
				else {
					for(int i=1; i<=11;i++) {
						Utils.msg(player, "help.line"+i, "", "");
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
					if (!player.hasPermission("lootchest.setholo") && !player.hasPermission("lootchest.admin")) {
						Utils.msg(player, "noPermission", "[Permission]", "lootchest.setholo");
						return false;
					}
					else if (!Main.getInstance().getData().isSet("chests." + args[1] + ".time")){
						Utils.msg(player, "chestDoesntExist", "[Chest]", args[1]);
					}
					else {
						Main.getInstance().getData().set("chests." + args[1] + ".holo", bc.toString());
						Utils.msg(player, "hologram_edited", "[Chest]", args[1]);
						Utils.restoreChest(args[1], false);
					}
				}
				else {
					for(int i=1; i<=11;i++) {
						Utils.msg(player, "help.line"+i, "", "");
					}
				}
			}
			else {
				for(int i=1; i<=11;i++) {
					Utils.msg(player, "help.line"+i, "", "");
				}
			}
		}
		return false;
	}

	
}
