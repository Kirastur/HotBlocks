package de.polarwolf.hotblocks.api;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class HotBlocksAPI {
	
	protected ConfigManager configManager;
	protected WorldManager worldManager;
	protected ModificationManager modificationManager;
	
	
	public HotBlocksAPI(ConfigManager configManager, WorldManager worldManager, ModificationManager modificationManager) {
		this.configManager = configManager;
		this.worldManager = worldManager;
		this.modificationManager = modificationManager;
	}
	
	
	// ConfigManager
	public Set<ConfigRule> getRules() {
		return configManager.getRules();
	}
	
	public void setConfig (Plugin newPlugin, String newConfigPath) {
		configManager.setConfig(newPlugin, newConfigPath);
	}

	public void reload() throws HotBlocksException {
		configManager.reload();
	}	
	
	
	// WorldManager
	public Set<World> getActiveWorlds() {
		return worldManager.getActiveWorlds();
	}

	public boolean isActiveWorld(World world) {
		return worldManager.isActiveWorld(world);
	}
			
	public void addWorld(World world) {
		worldManager.addWorld(world);
	}

	public void removeWorld(World world) {
		worldManager.removeWorld(world);
	}

		
	// ModificationManager Set-Management
	public Modification findModification(Location location) {
		return modificationManager.findModification(location);
	}
		
	public boolean isLocationModifying(Location location) {
		return modificationManager.isLocationModifying(location);
	}
		
	public Set<Modification> getModifications() {
		return modificationManager.getModifications();
	}
	
	public int getModificationCount() {
		return modificationManager.getModificationCount();
	}

	
	// ModificationManager Business-Logic
	public boolean addModification(Player player, Location location, ConfigRule rule) {
		return modificationManager.addModification(player, location, rule);
	}
				
	public boolean removeModification(Location location) {
		return modificationManager.removeModification(location);
	}
					
	public boolean checkLocation(Location location) {
		return modificationManager.checkLocation(null, location);
	}
						
	public int checkPlayer(Player player, Location targetLocation) {
		return modificationManager.checkPlayer(player, targetLocation);
	}
							
	public int checkWorld(World world) {
		return modificationManager.checkWorld(world);
	}
								
	public int cancelWorld(World world) {
		return modificationManager.cancelWorld(world);
	}

}
