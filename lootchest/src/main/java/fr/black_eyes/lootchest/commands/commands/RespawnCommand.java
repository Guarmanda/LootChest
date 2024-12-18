package fr.black_eyes.lootchest.commands.commands;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.BungeeChannel;
import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class RespawnCommand extends SubCommand {
	
	public RespawnCommand() {
		super("respawn", Collections.singletonList(ArgType.LOOTCHEST));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		lc.spawn(true);
		Utils.msg(sender, "succesfulyRespawnedChest", Constants.CHEST_PLACEHOLDER, chestName);
		if (lc.isRespawnCmdMsgEnabled()) {
			Block block = lc.getActualLocation().getBlock();
			String holo = lc.getHolo();
			String message = Utils.color((((Main.configs.noteCommandMsg.replace("[World]", block.getWorld().getName()).replace(Constants.CHEST_PLACEHOLDER, holo)).replace("[x]", block.getX() + "")).replace("[y]", block.getY() + "")).replace("[z]", block.getZ() + ""));
			if (Main.configs.noteBungeeBroadcast) {
				BungeeChannel.bungeeBroadcast(message);
			} else if (Main.configs.notePerWorldMessage) {
				for (Player p : block.getWorld().getPlayers()) {
					Utils.sendMultilineMessage(message, p);
				}
			} else {
				for (Player p : Bukkit.getOnlinePlayers()) {
					Utils.sendMultilineMessage(message, p);
				}
			}
		}
	}
}
