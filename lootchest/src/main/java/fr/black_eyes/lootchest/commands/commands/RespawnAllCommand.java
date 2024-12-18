package fr.black_eyes.lootchest.commands.commands;

import java.util.ArrayList;
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
		super("respawnall", new ArrayList<>(), Collections.singletonList(ArgType.WORLD));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (args.length ==2) {

			String worldName = args[1];
			for (final Lootchest l : Main.getInstance().getLootChest().values()) {
				if (!l.getWorld().equals(worldName)) {
					continue;
				}
				Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () ->
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
								l.spawn(true), 0L), 5L);
			}
			if (Main.configs.noteAllcmdWorldE) {
				if (Main.configs.noteBungeeBroadcast) {
					BungeeChannel.bungeeBroadcast(Utils.color(Main.configs.noteAllcmdMsgWorld));
				} else {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Utils.sendMultilineMessage(Main.configs.noteAllcmdMsgWorld.replace("[World]", worldName), p);
					}
				}
			}
			Utils.msg(sender, "AllChestsReloadedInWorld", "[World]", args[1]);
		} else if (args.length == 1) {
			for (final Lootchest l : Main.getInstance().getLootChest().values()) {
				Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () ->
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
								l.spawn(true), 0L), 5L);
			}
			if (Main.configs.noteAllcmdE) {
				if (Main.configs.noteBungeeBroadcast) {
					BungeeChannel.bungeeBroadcast(Utils.color(Main.configs.noteAllcmdMsg));
				} else {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Utils.sendMultilineMessage(Main.configs.noteAllcmdMsg, p);
					}
				}
			}
			Utils.msg(sender, "AllChestsReloaded", " ", " ");
		}
	}
}
