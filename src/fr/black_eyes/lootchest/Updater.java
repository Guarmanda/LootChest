package fr.black_eyes.lootchest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;





public class Updater {

    private JavaPlugin plugin;
    private int resourceId;
    private Logger log;
    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;
        String spigot_complete_id = "lootchest.61564";
        this.resourceId = 61564;
        this.log = Main.getInstance().getLogger();
        String installedVersion = Main.getInstance().getDescription().getVersion();
        this.getVersion(version -> {
            if (!version.equals(installedVersion) && Integer.parseInt(version.replace(".", "")) >  Integer.parseInt(installedVersion.replace(".", ""))) {
            	String lastverDownloadUrl = "https://www.spigotmc.org/resources/"+spigot_complete_id+"/history";
			    log.info( "§aA new version " + version + " was found on Spigot (your version: " + installedVersion + "). Please update me! <3 - Link: " + lastverDownloadUrl);

            } else {
            	log.info("§aThe plugin seems up to date.");
            }
        });
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
            	log.info("§cFailed to check for a update on spigot.");
            }
        });
    }
}





