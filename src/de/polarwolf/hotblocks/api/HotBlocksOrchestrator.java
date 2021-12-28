package de.polarwolf.hotblocks.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.events.EventManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.listener.ListenManager;
import de.polarwolf.hotblocks.logger.HotLogger;
import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class HotBlocksOrchestrator {

	public static final String PLUGIN_NAME = "HotBlocks";

	private final Plugin plugin;
	private final HotLogger hotLogger;
	private final EventManager eventManager;
	private final ConfigManager configManager;
	private final ModificationManager modificationManager;
	private final WorldManager worldManager;
	private final ListenManager listenManager;

	public HotBlocksOrchestrator(Plugin plugin) throws HotBlocksException {
		if (plugin != null) {
			this.plugin = plugin;
		} else {
			this.plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
		}
		if (!HotBlocksProvider.clearAPI()) {
			throw new HotBlocksException(this.plugin.getName(),
					"Can't start orchestrator, another instance is already running", null);
		}
		hotLogger = createHotLogger();
		eventManager = createEventManager();
		configManager = createConfigManager();
		modificationManager = createModificationManager();
		worldManager = createWorldManager();
		listenManager = createListenManager();
		HotBlocksProvider.setAPI(createAPI());
	}

	// Getter
	public Plugin getPlugin() {
		return plugin;
	}

	public HotLogger getHotLogger() {
		return hotLogger;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public ModificationManager getMofificationManager() {
		return modificationManager;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public ListenManager getListenManager() {
		return listenManager;
	}

	// Creator
	protected HotLogger createHotLogger() {
		return new HotLogger(this);
	}

	protected EventManager createEventManager() {
		return new EventManager(this);
	}

	protected ConfigManager createConfigManager() {
		return new ConfigManager(this);
	}

	protected ModificationManager createModificationManager() {
		return new ModificationManager(this);
	}

	protected WorldManager createWorldManager() {
		return new WorldManager(this);
	}

	protected ListenManager createListenManager() {
		return new ListenManager(this);
	}

	protected HotBlocksAPI createAPI() {
		return new HotBlocksAPI(this);
	}

	// Deactivation
	public void disable() {
		listenManager.unregisterListener();
		worldManager.disable();
		modificationManager.disable();
		HotBlocksProvider.clearAPI();
	}

	public boolean isDisabled() {
		return modificationManager.isDisabled();
	}

}
