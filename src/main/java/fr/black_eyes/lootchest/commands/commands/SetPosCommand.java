package fr.black_eyes.lootchest.commands.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.commands.ArgType;
import fr.black_eyes.lootchest.commands.SubCommand;

public class SetPosCommand extends SubCommand {
	
	public SetPosCommand() {
		super("setpos", Arrays.asList(ArgType.LOOTCHEST));
		setPlayerRequired(true);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Lootchest lc = Main.getInstance().getLootChest().get(args[1]);
		lc.setDirection(getCardinalDirection(player));
		lc.changepos(player.getLocation().getBlock().getLocation());
		lc.updateData();
		Utils.msg(sender, "changedPosition", Constants.cheststr, lc.getName());
	}

	public String getCardinalDirection(Player player) {
		float rotation = Utils.normalizeYaw(player.getLocation().getYaw());
		if (rotation > 135.0 || rotation <= -135.0) {
			return "NORTH";
		} else if (rotation > -135.0 && rotation < -45.0) {
			return "EAST";
		} else if (rotation >= -45.0 && rotation < 45.0) {
			return "SOUTH";
		} else if (rotation >= 45.0 && rotation <= 135.0) {
			return "WEST";
		}
		return null;
	}

}
