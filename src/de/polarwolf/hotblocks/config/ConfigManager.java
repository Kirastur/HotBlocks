package de.polarwolf.hotblocks.config;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.exception.HotBlocksException;

public class ConfigManager {
	
	protected Plugin plugin;
	protected String configPath;
	protected ConfigSection section;
	
	public ConfigManager(Plugin plugin, String configPath) {
		setConfig (plugin, configPath);
		section = new ConfigSection();
	}
	
	
	public void setConfig (Plugin newPlugin, String newConfigPath) {
		plugin = newPlugin;
		configPath = newConfigPath;
	}
	
	
	public void reload() throws HotBlocksException {
		plugin.reloadConfig();
		ConfigurationSection config = plugin.getConfig().getConfigurationSection(configPath);
		ConfigSection newSection = new ConfigSection();
		newSection.load(config);
		section = newSection;
	}


	public Set<ConfigRule> getRules() {
		return section.getRules();
	}

}
