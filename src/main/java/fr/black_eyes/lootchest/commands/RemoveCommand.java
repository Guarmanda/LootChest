package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;

public class RemoveCommand extends SubCommand {
	
	public RemoveCommand() {
		super("remove", 1);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[0];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if (lc == null) {
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		lc.deleteChest();
		Utils.msg(sender, "chestDeleted", Constants.cheststr, chestName);
	}
	
	@Override
	public String getUsage() {
		return "/lc edit <chestname>";
	}
}
