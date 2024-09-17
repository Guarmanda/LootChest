package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CopyCommand extends SubCommand {
	
	public CopyCommand() {
		super("copy", 2);
		setPlayerRequired(true);
	}
	
	@Override
	public String getUsage() {
		return "/lc copy <from chest> <to chest>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Lootchest lc = Main.getInstance().getLootChest().get(args[0]);
		Lootchest copy = Main.getInstance().getLootChest().get(args[1]);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, args[0]);
			return;
		}
		if (copy == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, args[1]);
			return;
		}
		Utils.copychest(lc,copy);
		copy.updateData();
		Utils.msg(sender, "copiedChest", "[Chest1]", lc.getName(), "[Chest2]", copy.getName());
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		return LootchestCommand.getChestNames();
	}
}
