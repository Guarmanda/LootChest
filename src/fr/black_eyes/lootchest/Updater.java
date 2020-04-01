package fr.black_eyes.lootchest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;




public class Updater {
	static Main inst = Main.getInstance();
	static Logger log = Main.getInstance().getLogger();
	static String spigot_complete_id = "lootchest.61564";
	static String spigot_id = "61564";
	
	private static JsonObject getLastVersionInfos() {
    	try {
            URL url = new URL("https://api.spiget.org/v2/resources/"+spigot_id+"/versions?size=1&sort=-releaseDate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "CHOCO-update-checker");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonElement element = new JsonParser().parse(reader);
            reader.close();
            JsonObject versionObject = element.getAsJsonArray().get(0).getAsJsonObject();
            return versionObject;
    	} catch (IOException e) {
    		log.info("Failed to check for a update on spigot.");
    	}
		return null;
    }
	
	public static String checkversion() {
		try {
	        String installedVersion = inst.getDescription().getVersion();
	        JsonObject versionInfos = getLastVersionInfos();
			String lastverDownloadUrl = "https://www.spigotmc.org/resources/"+spigot_complete_id+"/download?version=" + versionInfos.get("id").getAsString();
			String lastver = versionInfos.get("name").getAsString();
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
