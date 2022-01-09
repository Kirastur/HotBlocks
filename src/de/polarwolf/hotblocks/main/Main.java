package de.polarwolf.hotblocks.main;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.bstats.Metrics;
import de.polarwolf.hotblocks.commands.HotBlocksCommand;
import de.polarwolf.hotblocks.config.ConfigManager;

public final class Main extends JavaPlugin {

	// Please see modifications.ModificationManager for the Business-Logic

	public static final String COMMAND_NAME = "hotblocks";

	protected HotBlocksOrchestrator orchestrator = null;
	protected HotBlocksCommand hotBlocksCommand = null;

	protected void startupWorlds() {
		for (String worldName : ConfigManager.getStartupWorlds(this)) {
			World world = getServer().getWorld(worldName);
			if (world == null) {
				String s = String.format("Unknown world in Autostart: %s", worldName);
				getLogger().warning(s);
			} else {
				try {
					orchestrator.getWorldManager().addHotWorld(world, false);
					String s = String.format("World enabled via autostart: %s", worldName);
					getLogger().info(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

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

		// Check for passiveMode
		if (ConfigManager.isPassiveMode(this)) {
			getLogger().info("HotBlocks is in passive mode. You must register your own orchestrator.");
			return;
		}

		// StartupOrchestrator
		try {
			orchestrator = new HotBlocksOrchestrator(this);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Print debug state
		orchestrator.getHotLogger().printDebug("Debug messages are enabled");

		// Load Configuration Section
		orchestrator.getConfigManager().scheduleRedloadFoNextTick(orchestrator.getWorldManager(), null);

		// Enable startup Worlds
		startupWorlds();

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
