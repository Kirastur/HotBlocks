package de.polarwolf.hotblocks.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public record Coordinate(int x, int y, int z) {

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
		return new Coordinate(Integer.min(c1.x(), c2.x()), Integer.min(c1.y(), c2.y()), Integer.min(c1.z(), c2.z()));
	}

	public static Coordinate getMaximum(Coordinate c1, Coordinate c2) {
		return new Coordinate(Integer.max(c1.x(), c2.x()), Integer.max(c1.y(), c2.y()), Integer.max(c1.z(), c2.z()));
	}

}
