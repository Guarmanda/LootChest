package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class SetHoloCommand extends SubCommand {
	
	public SetHoloCommand() {
		super("setholo", Arrays.asList(ArgType.LOOTCHEST, ArgType.STRING));
	}
	
	@Override
	public String getUsage() {
		return "/lc setholo <chestname> <holotext>/none";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (!Main.getInstance().getConfig().getBoolean("UseHologram")) {
			return;
		}
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		String holoText = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		lc.setHolo(holoText);
		lc.updateData();
		Utils.msg(sender, "hologram_edited", Constants.CHEST_PLACEHOLDER, chestName);
		lc.spawn(false);
	}
}
