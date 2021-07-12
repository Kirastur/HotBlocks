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

import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class ListenManager implements Listener {
	
	protected final Plugin plugin;
	protected final WorldManager worldManager;
	protected final ModificationManager modificationManager;
	

	public ListenManager(Plugin plugin, WorldManager worldManager, ModificationManager modificationManager) {
		this.plugin = plugin;
		this.worldManager = worldManager;
		this.modificationManager = modificationManager;
	}
	
	
	public void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	
	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}
	
	
	protected void handlePlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location toLocation = event.getTo();
		if (worldManager.isActiveWorld(toLocation.getWorld())) {
			modificationManager.checkPlayer(player, toLocation);
		}
	}
		

	protected void handlePlayerTeleportEvent(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location toLocation = event.getTo();
		if (worldManager.isActiveWorld(toLocation.getWorld())) {
			modificationManager.checkPlayer(player, toLocation);
		}
	}


	protected void handleWorldUnloadEvent(WorldUnloadEvent event) {
		World world = event.getWorld();
		worldManager.removeWorld(world);
		modificationManager.cancelWorld(world);
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		try {
			handlePlayerMoveEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		try {
			handlePlayerTeleportEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onWorldUnloadEvent(WorldUnloadEvent event) {
		try {
			handleWorldUnloadEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
