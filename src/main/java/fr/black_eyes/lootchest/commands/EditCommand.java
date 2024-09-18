package fr.black_eyes.lootchest.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EditCommand extends SubCommand {
	
	private final LootchestCommand lootchestCommand;
	
	public EditCommand(LootchestCommand lootchestCommand) {
		super("edit", Arrays.asList(ArgType.LOOTCHEST));
		setPlayerRequired(true);
		this.lootchestCommand = lootchestCommand;
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String chestName = args[1];
		lootchestCommand.openMenu(player, chestName);
	}
}
