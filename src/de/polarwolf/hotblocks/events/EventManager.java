package de.polarwolf.hotblocks.events;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.logger.HotLogger;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.worlds.HotWorld;

/**
 * Handles the sending of Minecraft Events.
 */

public class EventManager {

	protected final Plugin plugin;
	protected final HotLogger hotLogger;

	public EventManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.hotLogger = orchestrator.getHotLogger();
	}

	protected HotWorld findDefaultHotWorld(World world, boolean startInPauseMode) {
		return new HotWorld(world, startInPauseMode);
	}

	public boolean sendRequestReloadEvent(CommandSender initiator) {
		HotBlocksReloadRequestEvent event = new HotBlocksReloadRequestEvent(initiator);
		plugin.getServer().getPluginManager().callEvent(event);
		return event.acknowledged;
	}

	public HotWorld sendActivateWorldEvent(World world, boolean startInPauseMode) {
		HotBlocksActivateWorldEvent event = new HotBlocksActivateWorldEvent(world, startInPauseMode);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			hotLogger.printDebug(String.format("Activating world \"%s\" was cancelled", world.getName()));
			return null;
		}
		if (event.hasHotWorld()) {
			hotLogger.printDebug(String.format("World \"%s\" activated with custom Handler", world.getName()));
			return event.getHotWorld();
		}
		hotLogger.printDebug(String.format("World \"%s\" activated with default Handler", world.getName()));
		return findDefaultHotWorld(world, startInPauseMode);
	}

	public ConfigRule sendCheckBlockEvent(Player player, HotWorld hotWorld, Coordinate blockCoordinate,
			TriggerEvent triggerEvent) {
		HotBlocksCheckBlockEvent event = new HotBlocksCheckBlockEvent(player, hotWorld, blockCoordinate, triggerEvent);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return null;
		}
		if (event.hasRule()) {
			ConfigRule rule = event.getRule();
			hotLogger.printDebug(String.format("Custom Rule \"%s\" found for blockcheck in world \"%s\"",
					rule.getName(), hotWorld.getWorld().getName()));
			return rule;
		}
		ConfigRule rule = hotWorld.findDefaultRule(player, blockCoordinate, triggerEvent);
		if (rule != null) {
			hotLogger.printDebug(String.format("Default Rule \"%s\" found for blockcheck in world \"%s\"",
					rule.getName(), hotWorld.getWorld().getName()));
		}
		return rule;
	}

	public Modification sendTriggerBlockEvent(Player player, HotWorld hotWorld, Coordinate blockCoordinate,
			ConfigRule rule) {
		HotBlocksTriggerBlockEvent event = new HotBlocksTriggerBlockEvent(player, hotWorld, blockCoordinate, rule);
		plugin.getServer().getPluginManager().callEvent(event);
		String playerName = "";
		if (player != null) {
			playerName = String.format(" (%s)", player.getName());
		}
		if (event.isCancelled()) {
			hotLogger.printDebug(String.format("Trigger \"%s\" in world \"%s\" was cancelled%s", rule.getName(),
					hotWorld.getWorld().getName(), playerName));
			return null;
		}
		if (event.hasModification()) {
			hotLogger.printDebug(String.format("Custom Trigger \"%s\" in world \"%s\" was created%s", rule.getName(),
					hotWorld.getWorld().getName(), playerName));
			return event.getModification();
		}
		Modification modification = hotWorld.generateDefaultModification(blockCoordinate, rule);
		if (modification != null) {
			hotLogger.printDebug(String.format("Default Trigger \"%s\" in world \"%s\" was created%s", rule.getName(),
					hotWorld.getWorld().getName(), playerName));
		}
		return modification;
	}

	public boolean sendModifyBlockEvent(Modification modification, BlockData blockData) {
		HotBlocksModifyBlockEvent event = new HotBlocksModifyBlockEvent(modification, blockData);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			hotLogger.printDebug(String.format("Modification with Rule \"%s\" in world \"%s\" was cancelled",
					modification.getRule().getName(), modification.getWorld().getName()));
			return false;
		} else {
			hotLogger.printDebug(String.format("Modification with Rule \"%s\" in world \"%s\" was confirmed",
					modification.getRule().getName(), modification.getWorld().getName()));
			return true;
		}
	}

}
