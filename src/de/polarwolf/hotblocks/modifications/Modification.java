package de.polarwolf.hotblocks.modifications;

import org.bukkit.Location;
import org.bukkit.World;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;

public class Modification {
	
	protected final World world;
	protected final Coordinate coordinate;
	protected final ConfigRule rule;
	protected int lifetime;
	
	
	public Modification(World world, Coordinate coordinate, ConfigRule rule) {
		this.world = world;
		this.coordinate = coordinate;
		this.rule = rule;
		this.lifetime = rule.getLifetime();
	}


	public World getWorld() {
		return world;
	}


	public Coordinate getCoordinate() {
		return coordinate;
	}


	public ConfigRule getRule() {
		return rule;
	}


	public int getLifetime() {
		return lifetime;
	}

	
	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

		
	public void decrementLifetime() {
		if (lifetime > 0) {
			lifetime = lifetime -1;
		}
	}
	

	public boolean isEndOfLife() {
		return (lifetime <= 0);
	}
	
	
	public boolean contains(Location location) {
		if (world != location.getWorld()) {
			return false;
		}
		return coordinate.equals(Coordinate.of(location));
	}
	
	
	public Location toLocation() {
		return coordinate.toLocation(world);
	}
			
}
