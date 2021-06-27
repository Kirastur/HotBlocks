package de.polarwolf.hotblocks.worlds;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;

public class WorldManager {
	
	protected Set<World> activeWorlds = new HashSet<>();
	
	
	public Set<World> getActiveWorlds() {
		return new HashSet<>(activeWorlds);
	}
	

	public boolean isActiveWorld(World world) {
		return activeWorlds.contains(world);
	}
	
	
	public void addWorld(World world) {
		activeWorlds.add(world);
	}
	
	
	public void removeWorld(World world) {
		activeWorlds.remove(world);
	}
	
}
