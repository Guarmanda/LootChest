package fr.black_eyes.lootchest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import fr.black_eyes.lootchest.googleThings.ByteArrayDataInput;
import fr.black_eyes.lootchest.googleThings.ByteArrayDataOutput;
import fr.black_eyes.lootchest.googleThings.ByteStreams;


/**
 * @author Black_Eyes
 * Broadcasts lootchest messages through bungeecord. Should works on 1.7+
 */
public class BungeeChannel implements PluginMessageListener {
	

	/**
	 * 
	 */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		String subchannel;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		subchannel = in.readUTF();

		if (subchannel.equals("SomeSubChannel")) {
			// Use the code sample in the 'Response' sections below to read
			// the data.
	    }
	}

	

	/**
	 * @param args The message to send
	 * @param p The player that should send the message. Selects a random player to send it. BungeeCord needs a player to broadcast a message.
	 */
	public static void sendPluginMsg(String args[], Player p) {

			if(p == null) {
				p = Bukkit.getOnlinePlayers().iterator().next();
				if(p==null) {
					return;
				}
			}
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			for(String arg : args) {
				out.writeUTF(arg);
			}
			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		
	}
	
	
	/**
	 * @param p
	 * @param server
	 * @param player
	 */
	public static void sendPlayerToServer(Player p, String server, String player){
		sendPluginMsg(new String[]{"ConnectOther", player, server}, null);
	}
	
	
	/**
	 * @param message
	 */
	public static void bungeeBroadcast(String message) {
		sendPluginMsg(new String[]{"Message", "ALL", message}, null);
	}
	
	

}
