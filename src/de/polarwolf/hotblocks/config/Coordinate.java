package de.polarwolf.hotblocks.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Coordinate {

	protected final int x;
	protected final int y;
	protected final int z;
	
	
	public Coordinate(int x, int y, int z ) {
		this.x = x;
		this.y = y;
		this.z = z;	
	}
	

	public int getX() {
		return x;
	}

	
	public int getY() {
		return y;
	}

	
	public int getZ() {
		return z;
	}
	
	
	public Vector toVector() {
		return new Vector(x, y, z);
	}
	
	
	public Location toLocation(World world) {
		return toVector().toLocation(world);
	}
	
	
	public static Coordinate of(Vector vector) {
		return new Coordinate(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}
	
	
	public static Coordinate of(Location location) {
		return new Coordinate(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	
	public static Coordinate getMinimum(Coordinate c1, Coordinate c2) {
		return new Coordinate(Integer.min(c1.getX(), c2.getX()),				
				              Integer.min(c1.getY(), c2.getY()),
				              Integer.min(c1.getZ(), c2.getZ()));
	}
	
	
	public static Coordinate getMaximum(Coordinate c1, Coordinate c2) {
		return new Coordinate(Integer.max(c1.getX(), c2.getX()),				
				              Integer.max(c1.getY(), c2.getY()),
				              Integer.max(c1.getZ(), c2.getZ()));
	}
	

	@Override
	public boolean equals(Object other) {
		if (this == other) {
		 	return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (other.getClass() != getClass()) {
			return false;
		}
		
		Coordinate otherCoordinate = (Coordinate)other;
		return ((x == otherCoordinate.getX()) &&
				(y == otherCoordinate.getY()) &&
				(z == otherCoordinate.getZ()));	
	}
	

	@Override
	public int hashCode() {
		return ((Math.abs(x & 511)      ) ^ (Integer.signum(x) <<  9) ^
			    (Math.abs(y & 511) << 10) ^ (Integer.signum(y) << 19) ^
				(Math.abs(z & 511) << 20) ^ (Integer.signum(z) << 29));
	}

}
