package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.BungeeChannel;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class RespawnCommand extends SubCommand {
	
	public RespawnCommand() {
		super("respawn", 1);
	}
	
	@Override
	public String getUsage() {
		return "/lc edit <chestname>";
	}
	
	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Lootchest lc = Main.getInstance().getLootChest().get(args[0]);
		if (lc == null){
			Utils.msg(sender, "chestDoesntExist", Constants.cheststr, args[1]);
			return;
		}
		lc.spawn( true);
		Utils.msg(sender, "succesfulyRespawnedChest", Constants.cheststr, args[1]);
		if(lc.getRespawn_cmd()) {
			Block block = lc.getActualLocation().getBlock();
			String holo = lc.getHolo();
			String message = Utils.color((((Main.configs.NOTE_command_msg.replace("[World]", block.getWorld().getName()).replace(Constants.cheststr, holo)).replace("[x]", block.getX()+"")).replace("[y]", block.getY()+"")).replace("[z]", block.getZ()+""));
			if(Main.configs.NOTE_bungee_broadcast) {
				BungeeChannel.bungeeBroadcast(message);
			}
			else if(Main.configs.NOTE_per_world_message) {
				for(Player p : block.getWorld().getPlayers()){
					Utils.sendMultilineMessage(message, p);
				}
			}else {
				for(Player p : Bukkit.getOnlinePlayers()) {
					Utils.sendMultilineMessage(message, p);
				}
			}
		}
	}
	
	@Override
	public List<String> getTabList(String[] args) {
		return LootchestCommand.getChestNames();
	}
}
