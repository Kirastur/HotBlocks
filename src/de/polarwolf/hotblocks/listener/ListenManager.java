package de.polarwolf.hotblocks.listener;

import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class ListenManager {
	
  private ListenManager() {
	    throw new IllegalStateException("Utility class");
	  }

	
	public static void registerEvents(Plugin plugin, WorldManager worldManager, ModificationManager modificationManager) {
    	plugin.getServer().getPluginManager().registerEvents(new WorldUnloadListener(worldManager, modificationManager), plugin);
	    plugin.getServer().getPluginManager().registerEvents(new PlayerMoveListener(worldManager, modificationManager), plugin);
	    plugin.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(worldManager, modificationManager), plugin);
	}

}
