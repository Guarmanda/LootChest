package fr.black_eyes.lootchest;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private File dataFile;
	private FileConfiguration data;
	private File configFile;
	private FileConfiguration config;
	private File langFile;
	private FileConfiguration lang;
	public Boolean PER_WORLD_MESSAGE;
	
	public void saveData() {
		try {
			getData().save(getDataF());
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
		
		}	
	}
	
	
	public void reloadConfig() {
		try {
			config.load(getConfigF());

			data.save(getDataF());
			data.load(getDataF());
			lang.load(getLangF());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		
		}
	}
	
	public void reloadData() {
		try {
			data.save(getDataF());
			data.load(getDataF());
		} catch (IOException | InvalidConfigurationException e) {
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
			Main.logInfo("Added config option '"+path+"' in config.yml");
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
			Main.logInfo("Added message '"+path+"' in lang.yml");
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
