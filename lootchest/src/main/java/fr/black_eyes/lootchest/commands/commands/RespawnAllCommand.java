package fr.black_eyes.lootchest.commands.commands;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.BungeeChannel;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class RespawnAllCommand extends SubCommand {
	
	public RespawnAllCommand() {
		super("respawnall", Collections.emptyList(), Collections.singletonList(ArgType.WORLD));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onCommand(CommandSender sender, String[] args) {
		String worldName = null;
		if (args.length ==2) {
			worldName = args[1];
		}
		if(!Lootchest.checkIfEnoughPlayersCommand()){
			Utils.msg(sender, "NotEnoughPlayers", "[Number]" , ""+Main.configs.minimumNumberOfPlayersForCommandSpawning);
			return;
		}
		for (final Lootchest l : Main.getInstance().getLootChest().values()) {
			if (worldName != null && !l.getWorld().equals(worldName)) {
				continue;
			}
			Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () ->
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
							l.spawn(true), 0L), 5L);
		}
		String message = null;
		if (Main.configs.noteAllcmdWorldE && worldName != null) {
			message = Main.configs.noteAllcmdMsgWorld.replace("[World]", worldName);
		}else if(Main.configs.noteAllcmdE && worldName == null) {
			message = Main.configs.noteAllcmdMsg;
			if (Main.configs.noteBungeeBroadcast) {
				BungeeChannel.bungeeBroadcast(message);
			} else {
				for (Player p : Bukkit.getOnlinePlayers()) {
					Utils.sendMultilineMessage(message, p);
				}
			}
		}
		if(worldName != null) {
			Utils.msg(sender, "AllChestsReloadedInWorld", "[World]", args[1]);
		} else {
			Utils.msg(sender, "AllChestsReloaded", " ", " ");
		}
	}
}
