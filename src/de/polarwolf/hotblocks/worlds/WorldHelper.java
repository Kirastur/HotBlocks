package de.polarwolf.hotblocks.worlds;

import java.util.List;

import org.bukkit.entity.Player;

import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.events.EventManager;
import de.polarwolf.hotblocks.logger.HotLogger;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.modifications.ModificationManager;

public final class WorldHelper {

	private final HotLogger hotLogger;
	private final EventManager eventManager;
	private final ConfigManager configManager;
	private final ModificationManager modificationManager;
	private final HotWorld hotWorld;

	public WorldHelper(HotLogger hotLogger, EventManager eventManager, ConfigManager configManager,
			ModificationManager modificationManager, HotWorld hotWorld) {
		this.hotLogger = hotLogger;
		this.eventManager = eventManager;
		this.configManager = configManager;
		this.modificationManager = modificationManager;
		this.hotWorld = hotWorld;
	}

	public HotLogger getHotLogger() {
		return hotLogger;
	}

	public ConfigRule sendCheckBlockEvent(Player player, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		return eventManager.sendCheckBlockEvent(player, hotWorld, blockCoordinate, triggerEvent);
	}

	public Modification sendTriggerBlockEvent(Player player, Coordinate blockCoordinate, ConfigRule rule) {
		return eventManager.sendTriggerBlockEvent(player, hotWorld, blockCoordinate, rule);
	}

	public List<ConfigRule> getRules() {
		return configManager.getRules();
	}

	public List<Modification> getModifications() {
		return modificationManager.getModifications();
	}

	public Modification findModification(Coordinate blockCoordinate) {
		return modificationManager.findModification(hotWorld.getWorld(), blockCoordinate);
	}

	public boolean isModifying(Coordinate blockCoordinate) {
		return modificationManager.isModifying(hotWorld.getWorld(), blockCoordinate);
	}

	public void addModification(Modification newModification) {
		modificationManager.addModification(newModification);
	}

	public boolean removeModification(Modification oldModification) {
		return modificationManager.removeModification(oldModification);
	}

	public int cancelModifications() {
		return modificationManager.cancelWorld(hotWorld.getWorld());
	}

	public boolean isDisabled() {
		return modificationManager.isDisabled();
	}
}
