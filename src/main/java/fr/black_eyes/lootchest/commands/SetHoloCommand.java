package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SetHoloCommand extends SubCommand {
	
	public SetHoloCommand() {
		super("setholo", 2);
	}
	
	@Override
	public String getUsage() {
		return "/lc setholo <chestname> <holotext> ...";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (!Main.getInstance().getConfig().getBoolean("UseHologram")) {
			return;
		}
		String chestName = args[0];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		String holoText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		lc.setHolo(holoText);
		lc.updateData();
		Utils.msg(sender, "hologram_edited", Constants.cheststr, chestName);
		lc.spawn(false);
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		if (args.length == 1) {
			return LootchestCommand.getChestNames();
		}
		return new LinkedList<>();
	}
}
