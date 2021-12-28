package de.polarwolf.hotblocks.events;

import org.bukkit.entity.Player;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.logger.HotLogger;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.worlds.HotWorld;

public final class EventHelper {

	private final EventManager eventManager;
	private final HotWorld hotWorld;
	private final HotLogger hotLogger;

	public EventHelper(EventManager eventManager, HotWorld hotWorld, HotLogger hotLogger) {
		this.eventManager = eventManager;
		this.hotWorld = hotWorld;
		this.hotLogger = hotLogger;
	}

	public ConfigRule sendCheckBlockEvent(Player player, Coordinate blockCoordinate) {
		return eventManager.sendCheckBlockEvent(player, hotWorld, blockCoordinate);
	}

	public Modification sendTriggerBlockEvent(Player player, Coordinate blockCoordinate, ConfigRule rule) {
		return eventManager.sendTriggerBlockEvent(player, hotWorld, blockCoordinate, rule);
	}

	public boolean sendModifyBlockEvent(Modification modification) {
		return eventManager.sendModifyBlockEvent(modification);
	}

	public HotLogger getHotLogger() {
		return hotLogger;
	}

}
