package de.polarwolf.hotblocks.listener;

import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class ListenManager {

	protected final Plugin plugin;
	protected final ConfigManager configManager;

	protected PlayerMoveListener playerMoveListener = null;
	protected PlayerTeleportListener playerTeleportListener = null;
	protected BlockPlaceListener blockPlaceListener = null;
	protected WorldUnloadListener worldUnloadListener = null;

	public ListenManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.configManager = orchestrator.getConfigManager();
	}

	protected void updateWorldUnloadListener(WorldManager worldManager) {
		if (!worldManager.getHotWorlds().isEmpty() && (worldUnloadListener == null)) {
			worldUnloadListener = new WorldUnloadListener(plugin, worldManager);
			worldUnloadListener.registerListener();
		}
		if (worldManager.getHotWorlds().isEmpty() && (worldUnloadListener != null)) {
			worldUnloadListener.unregisterListener();
			worldUnloadListener = null;
		}
	}

	protected void updatePlayerMoveListener(WorldManager worldManager) {
		if (configManager.hasListener(TriggerEvent.PLAYERMOVEEVENT) && (playerMoveListener == null)) {
			playerMoveListener = new PlayerMoveListener(plugin, worldManager);
			playerMoveListener.registerListener();
		}
		if (!configManager.hasListener(TriggerEvent.PLAYERMOVEEVENT) && (playerMoveListener != null)) {
			playerMoveListener.unregisterListener();
			playerMoveListener = null;
		}
	}

	protected void updatePlayerTeleportListener(WorldManager worldManager) {
		if (configManager.hasListener(TriggerEvent.PLAYERTELEPORTEVENT) && (playerTeleportListener == null)) {
			playerTeleportListener = new PlayerTeleportListener(plugin, worldManager);
			playerTeleportListener.registerListener();
		}
		if (!configManager.hasListener(TriggerEvent.PLAYERTELEPORTEVENT) && (playerTeleportListener != null)) {
			playerTeleportListener.unregisterListener();
			playerTeleportListener = null;
		}
	}

	protected void updateBlockPlaceListener(WorldManager worldManager) {
		if (configManager.hasListener(TriggerEvent.BLOCKPLACEEVENT) && (blockPlaceListener == null)) {
			blockPlaceListener = new BlockPlaceListener(plugin, worldManager);
			blockPlaceListener.registerListener();
		}
		if (!configManager.hasListener(TriggerEvent.BLOCKPLACEEVENT) && (blockPlaceListener != null)) {
			blockPlaceListener.unregisterListener();
			blockPlaceListener = null;
		}
	}

	public void updateListener(WorldManager worldManager) {
		updateWorldUnloadListener(worldManager);
		updatePlayerMoveListener(worldManager);
		updatePlayerTeleportListener(worldManager);
		updateBlockPlaceListener(worldManager);
	}

}
