package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

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
		Utils.msg(sender, "giveto", Constants.CHEST_PLACEHOLDER, chestName, "[Player]", playerName);
		Utils.msg(player, "givefrom", Constants.CHEST_PLACEHOLDER, chestName, "[Player]", sender.getName());
		LootChestUtils.fillInventory(lc, player.getInventory(), false, player);
	}
}
