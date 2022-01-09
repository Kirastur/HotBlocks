package de.polarwolf.hotblocks.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.worlds.WorldManager;

public class WorldUnloadListener implements Listener {

	protected final Plugin plugin;
	protected final WorldManager worldManager;

	public WorldUnloadListener(Plugin plugin, WorldManager worldManager) {
		this.plugin = plugin;
		this.worldManager = worldManager;
	}

	public void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
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
