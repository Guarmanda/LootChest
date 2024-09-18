package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GiveCommand extends SubCommand {
	
	public GiveCommand() {
		super("give", Arrays.asList(ArgType.LOOTCHEST, ArgType.PLAYER));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		String playerName = args[2];
		Player player = Bukkit.getPlayerExact(playerName);
		Utils.msg(sender, "giveto", Constants.cheststr, chestName, "[Player]", playerName);
		Utils.msg(player, "givefrom", Constants.cheststr, chestName, "[Player]", sender.getName());
		Utils.fillInventory(lc, player.getInventory(), false, player);
	}
}
