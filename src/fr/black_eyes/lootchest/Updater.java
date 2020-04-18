package fr.black_eyes.lootchest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.Bukkit;












public class Updater {
	static Main inst = Main.getInstance();
	static Logger log = Main.getInstance().getLogger();
	static String spigot_complete_id = "lootchest.61564";
	static String spigot_id = "61564";
	
	private static Object getLastVersionInfos() {
    	try {
            URL url = new URL("https://api.spiget.org/v2/resources/"+spigot_id+"/versions?size=1&sort=-releaseDate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "CHOCO-update-checker");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            if(!Bukkit.getVersion().contains("1.7")) {
	            com.google.gson.JsonElement element = new com.google.gson.JsonParser().parse(reader);
	            reader.close();
	            com.google.gson.JsonObject versionObject = element.getAsJsonArray().get(0).getAsJsonObject();
	            return versionObject;
            }
            else {
	            net.minecraft.util.com.google.gson.JsonElement element = new net.minecraft.util.com.google.gson.JsonParser().parse(reader);
	            reader.close();
	            net.minecraft.util.com.google.gson.JsonObject versionObject = element.getAsJsonArray().get(0).getAsJsonObject();
	            return versionObject;
            }
    	} catch (IOException e) {
    		log.info("Failed to check for a update on spigot.");
    	}
		return null;
    }
	
	public static String checkversion() {
		try {
	        String installedVersion = inst.getDescription().getVersion();
	        String lastver = null;
	        String lastverDownloadUrl = null;
	        if(!Bukkit.getVersion().contains("1.7")) {
		        com.google.gson.JsonObject versionInfos = (com.google.gson.JsonObject) getLastVersionInfos();
		        
				lastverDownloadUrl = "https://www.spigotmc.org/resources/"+spigot_complete_id+"/download?version=" + versionInfos.get("id").getAsString();
				lastver = versionInfos.get("name").getAsString();
	        }else {
	        	net.minecraft.util.com.google.gson.JsonObject versionInfos = (net.minecraft.util.com.google.gson.JsonObject) getLastVersionInfos();
		        
				lastverDownloadUrl = "https://www.spigotmc.org/resources/"+spigot_complete_id+"/download?version=" + versionInfos.get("id").getAsString();
				lastver = versionInfos.get("name").getAsString();
	        }
			if (!lastver.equals(installedVersion)) {
			    log.info( "§aA new version " + lastver + " was found on Spigot (your version: " + installedVersion + "). Please update me! <3 - Link: " + lastverDownloadUrl);
			}
			else {
				log.info("§aThe plugin seems up to date.");
			}
	    } catch (Exception ex) {
	        log.info("Failed to check for a update on spigot.");
	    }
	
	return null;
	}
}
