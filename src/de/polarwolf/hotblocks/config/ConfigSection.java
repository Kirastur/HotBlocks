package de.polarwolf.hotblocks.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.hotblocks.exception.HotBlocksException;

public class ConfigSection {
	
	protected Set<ConfigRule> ruleSet = new HashSet<>();
	
	
	public Set<ConfigRule> getRules() {
		return new HashSet<>(ruleSet);
	}
	

	protected ConfigRule createRule(String name, Map<String,String> parameters ) throws HotBlocksException {
		return new ConfigRule (name, parameters);
	}
	
	
	protected ConfigRule loadRule(ConfigurationSection config) throws HotBlocksException {
		String name = config.getName();
		Map <String,String> parameters = new HashMap<>();
		for (String attributeName : config.getKeys(false)) {
			String value = config.getString(attributeName);
			parameters.put(attributeName, value);
		}
		return createRule(name, parameters);
	}


	// You cannot call "load" directly, because the ConfigManager does not expose this object
	public void load(ConfigurationSection config) throws HotBlocksException {
		ruleSet.clear();
		for (String ruleName : config.getKeys(false)) {

			ConfigurationSection section = config.getConfigurationSection(ruleName);
			if (section == null) {
				throw new HotBlocksException(config.getName(), "Config Syntax Error", ruleName);
			}

			ConfigRule newRule = loadRule(section);
			ruleSet.add(newRule);
		}
	}
	
}
