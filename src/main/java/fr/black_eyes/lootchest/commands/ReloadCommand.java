package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Config;
import fr.black_eyes.lootchest.Files;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
	
	public ReloadCommand() {
		super("reload");
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Main main = Main.getInstance();
		Files configFiles = main.getConfigFiles();
		if (Config.getInstance().saveDataFileDuringReload) {
			Utils.saveAllChests();
		} else {
			configFiles.reloadData();
		}
		configFiles.reloadConfig();
		Main.configs = Config.getInstance(configFiles.getConfig());
		main.getPart().clear();
		if (!Bukkit.getVersion().contains("1.7")) {
			Main.getInstance().getLootChest().values().stream().forEach(chest -> chest.getHologram().remove());
		}
		Main.getInstance().getLootChest().clear();
		for (String keys : configFiles.getData().getConfigurationSection("chests").getKeys(false)) {
			String name = configFiles.getData().getString("chests." + keys + ".position.world");
			String randomname = name;
			if (configFiles.getData().getInt("chests." + keys + ".randomradius") > 0) {
				randomname = configFiles.getData().getString("chests." + keys + ".randomPosition.world");
			}
			if (name != null && Utils.isWorldLoaded(randomname) && Utils.isWorldLoaded(name)) {
				Main.getInstance().getLootChest().put(keys, new Lootchest(keys));
			} else {
				Main.getInstance().logInfo("&cCouldn't load chest " + keys + " : the world " + configFiles.getData().getString("chests." + keys + ".position.world") + " is not loaded.");
			}
		}
		
		for (final Lootchest l : Main.getInstance().getLootChest().values()) {
			if (Utils.isWorldLoaded(l.getWorld())) {
				Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), () -> {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
						if (!l.spawn(false)) {
							Utils.scheduleReSpawn(l);
							l.reactivateEffects();
						}
						
						
					}, 0L);
				}, 5L);
			}
		}
		Utils.msg(sender, "PluginReloaded", " ", " ");
	}
}
