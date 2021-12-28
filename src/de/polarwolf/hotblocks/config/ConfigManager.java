package de.polarwolf.hotblocks.config;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.commands.Message;
import de.polarwolf.hotblocks.events.EventManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.logger.HotLogger;

public class ConfigManager {

	public static final String SECTION_STARTUP = "startup";
	public static final String SECTION_RULES = "rules";
	public static final String PARAM_STARTUP_PASSIVEMODE = "passiveMode";
	public static final String PARAM_STARTUP_DEBUG = "debug";
	public static final boolean DEFAULT_PASSIVEMODE = false;
	public static final boolean DEFAULT_DEBUG = false;

	protected final Plugin plugin;
	protected final HotLogger hotLogger;
	protected final EventManager eventManager;
	protected ConfigSection section;
	protected ReloadScheduler reloadScheduler = null;

	public ConfigManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.hotLogger = orchestrator.getHotLogger();
		this.eventManager = orchestrator.getEventManager();
		section = new ConfigSection(plugin.getName());
	}

	public static boolean isPassiveMode(Plugin startupPlugin) {
		return startupPlugin.getConfig().getConfigurationSection(SECTION_STARTUP).getBoolean(PARAM_STARTUP_PASSIVEMODE,
				DEFAULT_PASSIVEMODE);
	}

	public static boolean isInitialDebug(Plugin startupPlugin) {
		return startupPlugin.getConfig().getConfigurationSection(SECTION_STARTUP).getBoolean(PARAM_STARTUP_DEBUG,
				DEFAULT_DEBUG);
	}

	public List<ConfigRule> getRules() {
		return section.getRules();
	}

	public ConfigSection buildConfigSectionFromLocalConfigFile(Plugin plugin, String configPath)
			throws HotBlocksException {
		ConfigurationSection rootFileSection = plugin.getConfig().getRoot();
		if (!rootFileSection.contains(configPath, true)) {
			return null;
		}
		if (!rootFileSection.isConfigurationSection(configPath)) {
			throw new HotBlocksException(plugin.getName(), "Illegal root section", configPath);
		}
		ConfigurationSection ruleFileSection = rootFileSection.getConfigurationSection(configPath);
		return new ConfigSection(plugin.getName(), ruleFileSection);
	}

	public void replaceConfig(ConfigSection newConfigSection, CommandSender initiator) {
		if (newConfigSection != null) {
			section = newConfigSection;
			int count = section.getRules().size();
			if (initiator != null) {
				String s = String.format(Message.RULES_LOADED.toString(), count);
				initiator.sendMessage(s);
			}
			if (!plugin.getServer().getConsoleSender().equals(initiator)) {
				hotLogger.printDebug(String.format("Configuration has %d rules loaded", count));
			}
		}
	}

	public void reload(CommandSender initiator) {
		reloadScheduler = null;
		plugin.reloadConfig();
		boolean isAcknowledged = eventManager.sendRequestReloadEvent(initiator);
		if (!isAcknowledged) {
			try {
				ConfigSection newSection = buildConfigSectionFromLocalConfigFile(plugin, SECTION_RULES);
				replaceConfig(newSection, initiator);
			} catch (HotBlocksException hbe) {
				if (initiator != null) {
					initiator.sendMessage(hbe.getMessage());
				}
			} catch (Exception e) {
				if (initiator != null) {
					initiator.sendMessage(Message.JAVA_EXCEPTOPN.toString());
				}
				e.printStackTrace();
			}
		}
	}

	public void scheduleRedloadFoNextTick() {
		if (reloadScheduler == null) {
			reloadScheduler = new ReloadScheduler(plugin, this);
		}
	}

}
