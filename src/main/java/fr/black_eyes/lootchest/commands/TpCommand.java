package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends SubCommand {
	
	public TpCommand() {
		super("tp", 1);
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
		Location loc = lc.getActualLocation();
		player.teleport(loc);
		Utils.msg(sender, "teleportedToChest", Constants.cheststr, args[1]);
	}
	
	@Override
	public String getUsage() {
		return "/lc edit <chestname>";
	}
}
