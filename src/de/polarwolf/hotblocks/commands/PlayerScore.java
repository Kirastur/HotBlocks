package de.polarwolf.hotblocks.commands;

import java.util.UUID;

public record PlayerScore(UUID playerUUID, int amount) implements Comparable<PlayerScore> {

	@Override
	public int compareTo(PlayerScore o) {
		if (amount < o.amount) {
			return -1;
		}
		if (amount > o.amount) {
			return 1;
		}
		return 0;
	}

}
