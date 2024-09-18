package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.BungeeChannel;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RespawnAllCommand extends SubCommand {
	
	public RespawnAllCommand() {
		super("respawnall", 1);
	}
	
	@Override
	public String getUsage() {
		return "/lc edit <chestname>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String worldName = args[0];
		World w = Bukkit.getWorld(worldName);
		if (w == null) {
			Utils.msg(sender, "worldDoesntExist", "[World]", worldName);
			return;
		}
		for (final Lootchest l : Main.getInstance().getLootChest().values()) {
			if (!l.getWorld().equals(worldName)) {
				continue;
			}
			Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					l.spawn(true);
				}, 0L);
			}, 5L);
		}
		if (Main.configs.NOTE_allcmd_world_e) {
			if (Main.configs.NOTE_bungee_broadcast) {
				BungeeChannel.bungeeBroadcast(Utils.color(Main.configs.NOTE_allcmd_msg_world));
			} else {
				for (Player p : Bukkit.getOnlinePlayers()) {
					Utils.sendMultilineMessage(Main.configs.NOTE_allcmd_msg_world.replace("[World]", worldName), p);
				}
			}
		}
		Utils.msg(sender, "AllChestsReloadedInWorld", "[World]", args[1]);
	}
	
	
	@Override
	public List<String> getTabList(String[] args) {
		if (args.length <= 1) {
			final List<String> completions = new ArrayList<>();
			
			for (World w : Bukkit.getWorlds()) {
				completions.add(w.getName());
			}
			return completions;
		}
		return new LinkedList<>();
	}
}
