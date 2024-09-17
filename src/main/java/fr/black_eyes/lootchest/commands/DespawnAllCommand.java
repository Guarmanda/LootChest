package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class DespawnAllCommand extends SubCommand {
	
	public DespawnAllCommand() {
		super("despawnall", 1);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String worldName = args[0];
		World w2 = Bukkit.getWorld(worldName);
		if(w2 == null) {
			Utils.msg(sender, "worldDoesntExist", "[World]", worldName);
			return;
		}
		for (final Lootchest l : Main.getInstance().getLootChest().values()) {
			if(!l.getWorld().equals(worldName)) {
				continue;
			}
			Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					l.spawn( false, true) ;
				}, 0L);
			}, 5L);
		}
		Utils.msg(sender, "AllChestsDespawnedInWorld", "[World]", worldName);
	}
	
	@Override
	public String getUsage() {
		return "/lc despawnall <world>";
	}
}