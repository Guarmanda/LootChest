package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPosCommand extends SubCommand {
	
	public SetPosCommand() {
		super("setpos", 1);
		setPlayerRequired(true);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Lootchest lc = Main.getInstance().getLootChest().get(args[0]);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, args[1]);
			return;
		}
		lc.setDirection(LootchestCommand.getCardinalDirection(player));
		lc.changepos(player.getLocation().getBlock().getLocation());
		lc.updateData();
		Utils.msg(sender, "changedPosition", Constants.cheststr, lc.getName());
	}
	
	@Override
	public String getUsage() {
		return "/lc edit <chestname>";
	}
}
