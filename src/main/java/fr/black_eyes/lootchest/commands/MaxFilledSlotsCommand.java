package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;

public class MaxFilledSlotsCommand extends SubCommand {
	
	public MaxFilledSlotsCommand() {
		super("maxfilledslots", 2);
		setPlayerRequired(true);
	}
	
	@Override
	public String getUsage() {
		return "/lc maxfilledslots <chestname> <slotnumber>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Lootchest lc = Main.getInstance().getLootChest().get(args[0]);
		if (lc == null) {
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, args[0]);
			return;
		}
		int maxFilledSlots;
		try {
			maxFilledSlots = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			return;
		}
		if (maxFilledSlots >= 0) {
			lc.setMaxFilledSlots(maxFilledSlots);
		}
		lc.updateData();
		Utils.msg(sender, "editedMaxFilledSlots", Constants.cheststr, lc.getName());
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		if (args.length == 1) {
			return LootchestCommand.getChestNames();
		}
		return new LinkedList<>();
	}
}
