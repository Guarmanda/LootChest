package fr.black_eyes.lootchest.commands.commands;

import java.util.Collections;

import org.bukkit.command.CommandSender;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;
import fr.black_eyes.simpleJavaPlugin.Utils;

public class RemoveCommand extends SubCommand {
	
	public RemoveCommand() {
		super("remove", Collections.singletonList(ArgType.LOOTCHEST));
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		lc.deleteChest();
		Utils.msg(sender, "chestDeleted", Constants.CHEST_PLACEHOLDER, chestName);
	}
}
