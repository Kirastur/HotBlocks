package de.polarwolf.hotblocks.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.worlds.WorldManager;

/**
 * Listen for Events when a player teleports
 */
public class PlayerTeleportListener implements Listener {

	protected final Plugin plugin;
	protected final WorldManager worldManager;

	public PlayerTeleportListener(Plugin plugin, WorldManager worldManager) {
		this.plugin = plugin;
		this.worldManager = worldManager;
	}

	public void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		try {
			Player player = event.getPlayer();
			Location toLocation = event.getTo();
			worldManager.checkPlayer(player, toLocation, TriggerEvent.PLAYERTELEPORTEVENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
