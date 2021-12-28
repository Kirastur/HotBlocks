package de.polarwolf.hotblocks.logger;

import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.config.ConfigManager;

public class HotLogger {

	private boolean debug = true;
	protected final Plugin plugin;

	public HotLogger(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.debug = ConfigManager.isInitialDebug(plugin);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void printDebug(String debugMessage) {
		if (isDebug()) {
			String s = String.format("DEBUG %s", debugMessage);
			plugin.getLogger().info(s);
		}
	}

	public void printInfo(String infoMessage) {
		plugin.getLogger().info(infoMessage);
	}

	public void printWarning(String warningMessage) {
		plugin.getLogger().warning(warningMessage);
	}

}
