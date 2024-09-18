package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SetPosCommand extends SubCommand {
	
	public SetPosCommand() {
		super("setpos", Arrays.asList(ArgType.LOOTCHEST));
		setPlayerRequired(true);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Lootchest lc = Main.getInstance().getLootChest().get(args[1]);
		lc.setDirection(LootchestCommand.getCardinalDirection(player));
		lc.changepos(player.getLocation().getBlock().getLocation());
		lc.updateData();
		Utils.msg(sender, "changedPosition", Constants.cheststr, lc.getName());
	}

}
