package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TpCommand extends SubCommand {
	
	public TpCommand() {
		super("tp", Arrays.asList(ArgType.LOOTCHEST));
		setPlayerRequired(true);
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String chestName = args[1];
		Lootchest lc = Main.getInstance().getLootChest().get(chestName);
		Location loc = lc.getActualLocation();
		player.teleport(loc);
		Utils.msg(sender, "teleportedToChest", Constants.cheststr, chestName);
	}
	
}
