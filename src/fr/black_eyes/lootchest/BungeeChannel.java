package fr.black_eyes.lootchest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;


public class BungeeChannel implements PluginMessageListener {
	


	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		String subchannel;
		if(Bukkit.getVersion().contains("1.7")) {
			net.minecraft.util.com.google.common.io.ByteArrayDataInput in = net.minecraft.util.com.google.common.io.ByteStreams.newDataInput(message);
			subchannel = in.readUTF();
		}else {
			com.google.common.io.ByteArrayDataInput in = com.google.common.io.ByteStreams.newDataInput(message);
			subchannel = in.readUTF();
		}
		if (subchannel.equals("SomeSubChannel")) {
			// Use the code sample in the 'Response' sections below to read
			// the data.
	    }
	}
	
	public static void sendPluginMsg(String args[], Player p) {
		if(Bukkit.getVersion().contains("1.7")) {
			if(p == null) {
				p = net.minecraft.util.com.google.common.collect.Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			}
			net.minecraft.util.com.google.common.io.ByteArrayDataOutput out = net.minecraft.util.com.google.common.io.ByteStreams.newDataOutput();
			for(String arg : args) {
				out.writeUTF(arg);
			}
			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		}else {
			if(p == null) {
				p = com.google.common.collect.Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
				if(p==null) {
					return;
				}
			}
			com.google.common.io.ByteArrayDataOutput out = com.google.common.io.ByteStreams.newDataOutput();
			for(String arg : args) {
				out.writeUTF(arg);
			}
			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());

				
		}
	}
	
	
	public static void sendPlayerToServer(Player p, String server, String player){
		sendPluginMsg(new String[]{"ConnectOther", player, server}, null);
	}
	
	
	public static void bungeeBroadcast(String message) {
		sendPluginMsg(new String[]{"Message", "ALL", message}, null);
	}
	
	

}
