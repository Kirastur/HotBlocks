package de.polarwolf.hotblocks.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class WorldUnloadListener implements Listener {
	
	protected final WorldManager worldManager;
	protected final ModificationManager modificationManager;
	

	public WorldUnloadListener(WorldManager worldManager, ModificationManager modificationManager) {
		this.worldManager = worldManager;
		this.modificationManager = modificationManager;
	}
	

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldUnloadEvent(WorldUnloadEvent event) {
		World world = event.getWorld();
		worldManager.removeWorld(world);
		modificationManager.cancelWorld(world);
	}

}
