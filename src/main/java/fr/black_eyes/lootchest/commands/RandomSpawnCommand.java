package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;

public class RandomSpawnCommand extends SubCommand {
	
	public RandomSpawnCommand() {
		super("randomspawn", 2);
	}
	
	@Override
	public String getUsage() {
		return "/lc randomspawn <chestname> <radius>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[0];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		int newradius = Integer.parseInt(args[1]);
		newradius = Math.max(newradius, 0);
		lc.setRadius(newradius);
		if(newradius> 0) {
			Utils.msg(sender, "chestRadiusSet", Constants.cheststr, chestName);
		}
		
		lc.despawn();
		if(newradius == 0) {
			lc.setRandomLoc(null);
			Utils.msg(sender, "disabledChestRadius", Constants.cheststr, chestName);
		}
		lc.spawn(true);
		lc.updateData();
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		if (args.length == 1) {
			return LootchestCommand.getChestNames();
		}
		return new LinkedList<>();
	}
}
