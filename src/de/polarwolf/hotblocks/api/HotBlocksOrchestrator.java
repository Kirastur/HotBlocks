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
	private final ListenManager listenManager;
	private final ModificationManager modificationManager;
	private final WorldManager worldManager;

	public HotBlocksOrchestrator(Plugin plugin) throws HotBlocksException {
		if (plugin != null) {
			this.plugin = plugin;
		} else {
			this.plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
		}
		testAPI();

		hotLogger = createHotLogger();
		eventManager = createEventManager();
		configManager = createConfigManager();
		listenManager = createListenManager();
		modificationManager = createModificationManager();
		worldManager = createWorldManager();
		setAPI();
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

	public ListenManager getListenManager() {
		return listenManager;
	}

	public ModificationManager getModificationManager() {
		return modificationManager;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	// Creator
	protected HotLogger createHotLogger() {
		return new HotLogger(plugin);
	}

	protected EventManager createEventManager() {
		return new EventManager(this);
	}

	protected ConfigManager createConfigManager() {
		return new ConfigManager(this);
	}

	protected ListenManager createListenManager() {
		return new ListenManager(this);
	}

	protected ModificationManager createModificationManager() {
		return new ModificationManager(this);
	}

	protected WorldManager createWorldManager() {
		return new WorldManager(this);
	}

	protected HotBlocksAPI createAPI() {
		return new HotBlocksAPI(this);
	}

	// API
	protected void testAPI() throws HotBlocksException {
		if (HotBlocksProvider.hasAPI()) {
			throw new HotBlocksException(this.plugin.getName(),
					"Can't start orchestrator, another instance is already running", null);
		}
	}

	protected void setAPI() {
		HotBlocksProvider.setAPI(createAPI());
	}

	protected void clearAPI() {
		HotBlocksProvider.setAPI(null);
	}

	// Deactivation
	public void disable() {
		worldManager.disable();
		modificationManager.disable();
		listenManager.updateListener(worldManager);
		clearAPI();
	}

}
