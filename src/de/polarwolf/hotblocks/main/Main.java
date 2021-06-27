package de.polarwolf.hotblocks.main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.hotblocks.api.HotBlocksAPI;
import de.polarwolf.hotblocks.bstats.Metrics;
import de.polarwolf.hotblocks.commands.HotBlocksCommand;
import de.polarwolf.hotblocks.commands.HotBlocksTabCompleter;
import de.polarwolf.hotblocks.commands.Message;
import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.listener.ListenManager;
import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class Main extends JavaPlugin {
	
	// Please see modifications.ModificationManager for the Business-Logic
	
	public static final String RULE_SECTION = "rules";
	public static final String COMMAND_NAME = "hotblocks";

	@Override
	public void onEnable() {
		
		// Prepare Configuration
		saveDefaultConfig();
		
		// Create ConfigManager
		ConfigManager configManager = new ConfigManager(this, RULE_SECTION);
		
		// Create WorldManager
		WorldManager worldManager = new WorldManager();

		// Create ModificationManager (the list of active blocks to be destroyed)
		ModificationManager modificationManager = new ModificationManager(this, configManager);
		
		// Register EventHandler (Listener)
		ListenManager.registerEvents(this, worldManager, modificationManager);
	    
	    // Initialize the API
	    HotBlocksAPI hotBlocksAPI = new HotBlocksAPI(configManager, worldManager, modificationManager);
		HotBlocksProvider.setAPI(hotBlocksAPI);
		
		// Register Command and TabCompleter
		HotBlocksCommand hotBlocksCommand = new HotBlocksCommand(this, hotBlocksAPI);
		getCommand(COMMAND_NAME).setExecutor(hotBlocksCommand);
		getCommand(COMMAND_NAME).setTabCompleter(new HotBlocksTabCompleter(hotBlocksCommand));
		
		// Enable bStats Metrics
		// Please download the bstats-code direct form their homepage
		// or disable the following instruction
		new Metrics(this, Metrics.PLUGINID_HOTBLOCKS);

		// Load Configuration Section
		try {
			configManager.reload();
			int count = configManager.getRules().size();
			getLogger().info(Integer.toString(count) + Message.RULES_LOADED.toString());
		} catch (HotBlocksException e) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR " + e.getMessage());
			getLogger().warning(Message.LOAD_ERROR.toString());
		}
	}
	
}
