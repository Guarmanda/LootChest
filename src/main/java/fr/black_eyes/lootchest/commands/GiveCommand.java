package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends SubCommand {
	
	public GiveCommand() {
		super("give", 1);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[0];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		String playerName = args[1];
		Player player = Bukkit.getPlayerExact(playerName);
		if(player == null) {
			Utils.msg(sender, "PlayerIsNotOnline", "[Player]", playerName);
		}
		else {
			Utils.msg(sender, "giveto", Constants.cheststr, args[1], "[Player]", playerName);
			Utils.msg(player, "givefrom", Constants.cheststr, chestName, "[Player]", sender.getName());
			Utils.fillInventory(lc, player.getInventory(), false, player);
		}
	}
	
	@Override
	public String getUsage() {
		return "/lc give <chestname> <player>";
	}
}