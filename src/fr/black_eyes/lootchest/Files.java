package fr.black_eyes.lootchest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Files {
	private File dataFile;
	private FileConfiguration data;
	private File configFile;
	private FileConfiguration config;
	private File langFile;
	private FileConfiguration lang;
	public Boolean PER_WORLD_MESSAGE;
	private Main main;
	public Files() {
		main = Main.getInstance();
	}
	
	
	
	public void saveData() {
		try {
			getData().save(getDataF());
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
		
		}	
	}
	
	
	public void reloadConfig() {
		try {
			configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
			config.load(getConfigF());

			data.save(getDataF());
			//data.load(getDataF());
			lang.load(getLangF());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		
		}
	}
	
	public void reloadData() {
		try {
			data.save(getDataF());
			//data.load(getDataF());
		} catch (IOException /*| InvalidConfigurationException*/ e) {
			e.printStackTrace();
		}
	}
	
	public void saveConfig() {
		try {
			config.save(getConfigF());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveLang() {
		try {
			lang.save(getLangF());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//function to update config on new version
	public void setConfig(String path, Object value) {
		if(this.getConfig().isSet(path))
			return;
		else
			getConfig().set(path, value);
			main.logInfo("Added config option '"+path+"' in config.yml");
			try {
				getConfig().save(getConfigF());
				getConfig().load(getConfigF());
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
	}
	
	//function to edit lang file on new version
	public void setLang(String path, Object value) {
		if(this.getLang().isSet(path))
			return;
		else
			getLang().set(path, value);
			main.logInfo("Added message '"+path+"' in lang.yml");
			try {
				getLang().save(getLangF());
				getLang().load(getLangF());
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
	}
	
	//file initializations
	private File getDataF() {
		return dataFile;
	}
	private File getConfigF() {
		return configFile;
	}
	public File getLangF() {
		return langFile;
	}
	public FileConfiguration getData() {
		return data;
	}
	public FileConfiguration getConfig() {
		return config;
	}
	public FileConfiguration getLang() {
		return lang;
	}
	
	

	
	public boolean initFiles() {
		//config
	    configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
	    langFile = new File(Main.getInstance().getDataFolder(), "lang.yml");
	    dataFile = new File(Main.getInstance().getDataFolder(), "data.yml");
	    if (!configFile.exists()) {
	        configFile.getParentFile().mkdirs();
	        Main.getInstance().saveResource("config.yml", false);
	    }
	    config= new YamlConfiguration();
	    try {
	        config.load(configFile);
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	    
	    //lang
	    if (!langFile.exists()) {
	        langFile.getParentFile().mkdirs();
	        Main.getInstance().saveResource("lang.yml", false);
	    }
	    lang= new YamlConfiguration();
	    try {
	        lang.load(langFile);
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	   
	    //data
	    if (!dataFile.exists()) {
	        dataFile.getParentFile().mkdirs();
	        Main.getInstance().saveResource("data.yml", false);
	    }
	    if(dataFile.length() == 0) {
	    	main.logInfo("&cInvalid data file detected! Finding backup right now...");
			File directoryPath = new File(Main.getInstance().getDataFolder() + "/backups/");
			List<String> contents = Arrays.asList(directoryPath.list());
			int i;
			//finding valid backup name
			for(i=0; contents.contains(i+"data.yml");i++);
			
			if((new File(Main.getInstance().getDataFolder() + "/backups/", (i-1)+"data.yml")).length() ==0) {
				main.logInfo("&cDidn't find old enough backups, creating new data file!");
			    Main.getInstance().saveResource("data.yml", true);
			}
			else {
				main.logInfo("&aFound an old enough backup. If it doesn't works, contact developper (all infos in spigot plugin page)");
				Path source = Paths.get(Main.getInstance().getDataFolder() + "/data.yml");
			    Path target = Paths.get(Main.getInstance().getDataFolder() + "/backups/"+(i-2)+"data.yml");
			    try {
			    	java.nio.file.Files.copy(target, source,  StandardCopyOption.REPLACE_EXISTING);
			    } catch (IOException e1) {
			        e1.printStackTrace();
			    }
				dataFile = new File(Main.getInstance().getDataFolder(), "/data.yml");
			}
	    }
	    data= new YamlConfiguration();
	    try {
	        data.load(dataFile);
	    } catch ( Exception e) {
	    	e.printStackTrace();
	        return false;
	    }
		return true;
	}
}
