package de.polarwolf.hotblocks.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class ListenManager implements Listener {

	protected final Plugin plugin;
	protected final WorldManager worldManager;

	public ListenManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.worldManager = orchestrator.getWorldManager();
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
			worldManager.checkPlayer(player, toLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		try {
			Player player = event.getPlayer();
			Location toLocation = event.getTo();
			worldManager.checkPlayer(player, toLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onWorldUnloadEvent(WorldUnloadEvent event) {
		try {
			World world = event.getWorld();
			worldManager.removeHotWorld(world);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
