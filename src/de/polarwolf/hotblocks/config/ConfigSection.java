package de.polarwolf.hotblocks.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.hotblocks.exception.HotBlocksException;

/**
 * The ConfigSection (Ruleset) is an unordered list of rules. Per default the
 * ruleset is searched in the HotBlocks plugin config.yml file in a section
 * called "rules". With the API you can point to another section.
 */
public class ConfigSection {

	protected final String name;
	protected List<ConfigRule> rules = new ArrayList<>();

	protected ConfigSection(String name) {
		this.name = name;
	}

	public ConfigSection(String name, ConfigurationSection fileSection) throws HotBlocksException {
		this.name = name;
		loadFromFile(fileSection);
	}

	public List<ConfigRule> getRules() {
		return new ArrayList<>(rules);
	}

	protected void addRule(ConfigRule newRule) {
		rules.add(newRule);
	}

	protected void addRule(String name, Map<String, String> parameters) throws HotBlocksException {
		ConfigRule newRule = new ConfigRule(name, parameters);
		addRule(newRule);
	}

	protected void addRule(ConfigurationSection fileSection) throws HotBlocksException {
		ConfigRule newRule = new ConfigRule(fileSection);
		addRule(newRule);
	}

	protected void loadFromFile(ConfigurationSection fileSection) throws HotBlocksException {
		for (String myRuleName : fileSection.getKeys(false)) {
			if (!fileSection.contains(myRuleName, true)) {
				continue;
			}
			if (!fileSection.isConfigurationSection(myRuleName)) {
				throw new HotBlocksException(fileSection.getName(), "Illegal rule section", myRuleName);
			}
			ConfigurationSection myRuleFileSection = fileSection.getConfigurationSection(myRuleName);
			addRule(myRuleFileSection);
		}
	}

	public boolean hasListener(TriggerEvent triggerEvent) {
		for (ConfigRule myRule : rules) {
			if (myRule.hasListener(triggerEvent)) {
				return true;
			}
		}
		return false;
	}

}
