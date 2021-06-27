package de.polarwolf.hotblocks.config;

public class Region {
	
	protected final Coordinate minCorner;
	protected final Coordinate maxCorner;
	
	
	public Region(Coordinate corner1, Coordinate corner2) {
		this.minCorner = Coordinate.getMinimum(corner1, corner2);
		this.maxCorner = Coordinate.getMaximum(corner1, corner2);
	}


	public Coordinate getMinCorner() {
		return minCorner;
	}


	public Coordinate getMaxCorner() {
		return maxCorner;
	}
	

	public boolean contains(Coordinate coordinate) {
		return ((coordinate.getX() >= minCorner.getX()) &&
				(coordinate.getY() >= minCorner.getY()) &&
				(coordinate.getZ() >= minCorner.getZ()) &&
		        (coordinate.getX() <= maxCorner.getX()) &&
				(coordinate.getY() <= maxCorner.getY()) &&
				(coordinate.getZ() <= maxCorner.getZ()));
	}

}
