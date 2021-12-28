package de.polarwolf.hotblocks.config;

public record Region(Coordinate minCorner, Coordinate maxCorner) {

	public boolean contains(Coordinate coordinate) {
		return ((coordinate.x() >= minCorner.x()) && (coordinate.y() >= minCorner.y())
				&& (coordinate.z() >= minCorner.z()) && (coordinate.x() <= maxCorner.x())
				&& (coordinate.y() <= maxCorner.y()) && (coordinate.z() <= maxCorner.z()));
	}

	public static Region of(Coordinate corner1, Coordinate corner2) {
		Coordinate minCorner = Coordinate.getMinimum(corner1, corner2);
		Coordinate maxCorner = Coordinate.getMaximum(corner1, corner2);
		return new Region(minCorner, maxCorner);
	}

}
