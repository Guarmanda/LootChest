package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class DespawnAllCommand extends SubCommand {
	
	public DespawnAllCommand() {
		super("despawnall", Arrays.asList(), Arrays.asList(ArgType.WORLD));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if(args.length == 2) {
			String worldName = args[1];
			for (final Lootchest l : Main.getInstance().getLootChest().values()) {
				if (!l.getWorld().equals(worldName)) {
					continue;
				}
				Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () ->
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
								l.spawn(false, true), 0L), 5L);
			}
			Utils.msg(sender, "AllChestsDespawnedInWorld", "[World]", worldName);
		}else if(args.length == 1) {
			for (final Lootchest l : Main.getInstance().getLootChest().values()) {
				Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () ->
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
								l.spawn(false, true), 0L), 5L);
			}
			Utils.msg(sender, "AllChestsDespawned", " ", " ");
		}
		
		
	}
}
