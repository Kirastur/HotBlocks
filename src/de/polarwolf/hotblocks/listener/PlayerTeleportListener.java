package de.polarwolf.hotblocks.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class PlayerTeleportListener implements Listener {
	
	protected final WorldManager worldManager;
	protected final ModificationManager modificationManager;
	

	public PlayerTeleportListener(WorldManager worldManager, ModificationManager modificationManager) {
		this.worldManager = worldManager;
		this.modificationManager = modificationManager;
	}
	

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		Location toLocation = event.getTo();
		if ((worldManager.isActiveWorld(toLocation.getWorld())) && (!event.isCancelled())) {
			modificationManager.checkPlayer(player, toLocation);
		}
	}

}
