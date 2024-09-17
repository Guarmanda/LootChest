package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class TpCommand extends SubCommand {
	
	public TpCommand() {
		super("tp", 1);
		setPlayerRequired(true);
	}
	
	@Override
	public String getUsage() {
		return "/lc tp <chestname>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String chestName = args[0];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		Location loc = lc.getActualLocation();
		player.teleport(loc);
		Utils.msg(sender, "teleportedToChest", Constants.cheststr, chestName);
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		return LootchestCommand.getChestNames();
	}
}
