package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EditCommand extends SubCommand {
	
	private final LootchestCommand lootchestCommand;
	
	public EditCommand(LootchestCommand lootchestCommand) {
		super("edit", 1);
		setPlayerRequired(true);
		this.lootchestCommand = lootchestCommand;
	}
	
	@Override
	public String getUsage() {
		return "/lc edit <chestname>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String chestName = args[0];
		if (!Main.getInstance().getLootChest().containsKey(chestName)) {
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, chestName);
			return;
		}
		lootchestCommand.openMenu(player, chestName);
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		return LootchestCommand.getChestNames();
	}
}
