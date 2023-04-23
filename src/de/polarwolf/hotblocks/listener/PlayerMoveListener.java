package de.polarwolf.hotblocks.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.worlds.WorldManager;

/**
 * Listen for Events when a player moves
 */
public class PlayerMoveListener implements Listener {

	protected final Plugin plugin;
	protected final WorldManager worldManager;

	public PlayerMoveListener(Plugin plugin, WorldManager worldManager) {
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
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		try {
			Player player = event.getPlayer();
			Location toLocation = event.getTo();
			worldManager.checkPlayer(player, toLocation, TriggerEvent.PLAYERMOVEEVENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
