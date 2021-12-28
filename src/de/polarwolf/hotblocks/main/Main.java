package de.polarwolf.hotblocks.main;

import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.bstats.Metrics;
import de.polarwolf.hotblocks.commands.HotBlocksCommand;
import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;

public final class Main extends JavaPlugin {

	// Please see modifications.ModificationManager for the Business-Logic

	public static final String COMMAND_NAME = "hotblocks";

	protected HotBlocksOrchestrator orchestrator = null;
	protected HotBlocksCommand hotBlocksCommand = null;

	@Override
	public void onEnable() {

		// Prepare Configuration
		saveDefaultConfig();

		// Register Command and TabCompleter
		hotBlocksCommand = new HotBlocksCommand(this, COMMAND_NAME);

		// Enable bStats Metrics
		// Please download the bstats-code direct form their homepage
		// or disable the following instruction
		new Metrics(this, Metrics.PLUGINID_HOTBLOCKS);

		if (ConfigManager.isPassiveMode(this)) {
			getLogger().info("HotBlocks is in passive mode. You must register your own orchestrator.");
			return;
		}

		// StartupOrchestrator
		try {
			orchestrator = new HotBlocksOrchestrator(null);
		} catch (HotBlocksException e) {
			e.printStackTrace();
			return;
		}

		// Print debug state
		orchestrator.getHotLogger().printDebug("Debug messages are enabled");

		// Load Configuration Section
		orchestrator.getConfigManager().scheduleRedloadFoNextTick();

		// Print final message
		getLogger().info("Some blocks are now hot.");
	}

	@Override
	public void onDisable() {
		if (orchestrator != null) {
			orchestrator.disable();
			orchestrator = null;
		}
	}

}
