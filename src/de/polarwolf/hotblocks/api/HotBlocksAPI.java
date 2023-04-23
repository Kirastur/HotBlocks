package de.polarwolf.hotblocks.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.ConfigSection;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.HotWorld;
import de.polarwolf.hotblocks.worlds.WorldManager;

/**
 * API for the HotBlocks Plugin
 */
public class HotBlocksAPI {

	private final ConfigManager configManager;
	private final ModificationManager modificationManager;
	private final WorldManager worldManager;

	public HotBlocksAPI(HotBlocksOrchestrator orchestrator) {
		this.configManager = orchestrator.getConfigManager();
		this.modificationManager = orchestrator.getModificationManager();
		this.worldManager = orchestrator.getWorldManager();
	}

	// ConfigManager
	/**
	 * Gets a list of all rules as configured in the configuration file.
	 *
	 * @return List of configured rules
	 * @see ConfigManager ConfigManager
	 */
	public List<ConfigRule> getRules() {
		return configManager.getRules();
	}

	/**
	 * Build the configuration-object from the plugin's config.yml.
	 *
	 * @param plugin     The plugin to determine which config.yml should be used.
	 * @param configPath The path inside the config.yml where the HotBlocks config
	 *                   is placed, e.g. "rules".
	 * @return A new configuration-object which can be used for "replaceConfig"
	 * @throws HotBlocksException An exception is thrown if the configuration is
	 *                            invalid
	 * @see ConfigManager ConfigManager
	 */
	public ConfigSection buildConfigSectionFromLocalConfigFile(Plugin plugin, String configPath)
			throws HotBlocksException {
		return configManager.buildConfigSectionFromLocalConfigFile(plugin, configPath);
	}

	/**
	 * Set a new Ruleset. The old Ruleset is thrown away. Existing Modification are
	 * still using the old rules, the new Ruleset affects only new Modifications.
	 * You should call this API-function only if you are requested to do so by
	 * receiving a {@link de.polarwolf.hotblocks.events.HotBlocksReloadRequestEvent
	 * HotBlocksReloadRequestEvent}, and you have acknowledged this event. If the
	 * initiator is set, a short chat-message is sent to him.
	 *
	 * @param newConfigSection The new ruleset
	 * @param initiator        Receiver of a confirmation notification (optional)
	 * @see ConfigManager ConfigManager
	 */
	public void replaceConfig(ConfigSection newConfigSection, CommandSender initiator) {
		configManager.replaceConfig(worldManager, initiator, newConfigSection);
	}

	/**
	 * Triggers a reload of the configuration. The initiator is optional and is only
	 * used to send him a success- or failure-message.
	 *
	 * @param initiator Receiver of a confirmation notification (optional)
	 * @see ConfigManager ConfigManager
	 */
	public void reload(CommandSender initiator) {
		configManager.reload(worldManager, initiator);
	}

	/**
	 * Triggers a reload of the configuration on the next Minecraft-Tick. Only used
	 * on server-start.
	 *
	 * @param initiator Receiver of a confirmation notification (optional)
	 * @see ConfigManager ConfigManager
	 */
	public void scheduleRedloadFoNextTick(CommandSender initiator) {
		configManager.scheduleRedloadFoNextTick(worldManager, initiator);
	}

	// ModificationManager
	/**
	 * Get a list of all currently active Modifications. Most of the
	 * Modification-Properties are controlled by this object.
	 *
	 * @return List of active modifications
	 * @see ModificationManager ModificationManager
	 */
	public List<Modification> getModifications() {
		return modificationManager.getModifications();
	}

	/**
	 * Find a Modification for the given location. The position is defined by the
	 * HotBlocks internal {@link de.polarwolf.hotblocks.config.Coordinate
	 * Coordinate} notation. A Location matches to the modification, if it belongs
	 * to the same word and the integer coordinates (Floor) are identical (which
	 * means they point to the same block)
	 *
	 * @param world           The word the location is in. You can get the word
	 *                        using Location.getWorld().
	 * @param blockCoordinate The X/Y/Z of the Location. You can use
	 *                        {@link de.polarwolf.hotblocks.config.Coordinate#of(Location)
	 *                        Coordinate.of(Location)}
	 * @return The current Modification for this block, or NULL if this block has no
	 *         Modification attached.
	 * @see ModificationManager ModificationManager
	 */
	public Modification findModification(World world, Coordinate blockCoordinate) {
		return modificationManager.findModification(world, blockCoordinate);
	}

	/**
	 * Check if a given location has a Modification. The position is defined by the
	 * HotBlocks internal {@link de.polarwolf.hotblocks.config.Coordinate
	 * Coordinate} notation. A Location matches to the modification, if it belongs
	 * to the same word and the integer coordinates (Floor) are identical (which
	 * means they point to the same block)
	 *
	 * @param world           The word the location is in. You can get the word
	 *                        using Location.getWorld().
	 * @param blockCoordinate The X/Y/Z of the Location. You can use
	 *                        {@link de.polarwolf.hotblocks.config.Coordinate#of(Location)
	 *                        Coordinate.of(Location)}
	 * @return TRUE if this block has a Modification attached, otherwise FALSE
	 * @see ModificationManager ModificationManager
	 */
	public boolean isModifying(World world, Coordinate blockCoordinate) {
		return modificationManager.isModifying(world, blockCoordinate);
	}

	/**
	 * Add a new Modification. A block should only have only one Modification
	 * running at the same time. The normal
	 * {@link de.polarwolf.hotblocks.worlds.HotWorld HotWorld} cares about this, but
	 * here you are responsible on your own to make sure that only one Modification
	 * for the block is running (except you explicitly want these chaotic
	 * side-effects). You can create the Modification by directly creating the
	 * {@link de.polarwolf.hotblocks.modifications.Modification Modification}-object
	 * or by calling the
	 * {@link de.polarwolf.hotblocks.worlds.HotWorld#generateModification(Player, Coordinate, ConfigRule)
	 * HotWorld.generateModification} function.
	 *
	 * @param newModification The new Modification which should be processed by
	 *                        adding them to the internal Modifications-List.
	 * @see ModificationManager ModificationManager
	 */
	public void addModification(Modification newModification) {
		modificationManager.addModification(newModification);
	}

	/**
	 * Removes a Modification so that the block modification will not occure.
	 *
	 * @param oldModification The Modification which should be removed
	 * @return TRUE if the Modification was successful removed, FALSE otherwise
	 *         (e.g. the given modification was not on the internal modification
	 *         list and therefore not active)
	 * @see ModificationManager ModificationManager
	 */
	public boolean removeModification(Modification oldModification) {
		return modificationManager.removeModification(oldModification);
	}

	/**
	 * Removes all modification for the given world, so that no block will be
	 * modified any more. Remember to set the HotWorld to pause first to avoid new
	 * Modifications to be created (e.g. by the the Player movement-check) remember
	 * to set the Hotworld to pause first).
	 *
	 * @param world World where all Modifications should be removed
	 * @return The number of Modifications which were removed
	 * @see ModificationManager ModificationManager
	 */
	public int cancelModifications(World world) {
		return modificationManager.cancelWorld(world);
	}

	// WorldManager
	/**
	 * Get a list of active Worlds. Active Worlds are worlds where the HotBlocks
	 * plugin monitors the PlayerMoveEvent and PlayerTeleportEvent and checks the
	 * blocks below the target location for modification rules to trigger.
	 *
	 * @return List of active Worlds
	 * @see WorldManager WorldManager
	 */
	public List<HotWorld> getHotWorlds() {
		return worldManager.getHotWorlds();
	}

	/**
	 * Find the HotWorld object for a given world. If the world is not enabled
	 * (which means a HotWorld object does not exists for this world), null is
	 * returned.
	 *
	 * @param world World to look for
	 * @return HotWorld-object for the given world, or NULL if none was found.
	 * @see WorldManager WorldManager
	 */
	public HotWorld findHotWorld(World world) {
		return worldManager.findHotWorld(world);
	}

	/**
	 * Check if the world is enabled by having an associated
	 * {@link de.polarwolf.hotblocks.worlds.HotWorld HotWorld}-object.
	 *
	 * @param world World to look for
	 * @return TRUE if the word has an associated HotWorld-object, otherwise FALSE
	 * @see WorldManager WorldManager
	 */
	public boolean hasHotWorld(World world) {
		return worldManager.hasHotWorld(world);
	}

	/**
	 * Enable a World for HotBlocks functionality by creating the associated
	 * HotWorld object. All HotWorlds are managed by an internal list in the
	 * WorldManager, so you can get this HotWorld object again by calling the
	 * {@link de.polarwolf.hotblocks.api.HotBlocksAPI#findHotWorld(World)
	 * findHotWorld} API-function.
	 *
	 * @param world            World to enable HotBlocks there
	 * @param startInPauseMode
	 * @return new created HotWorld object
	 * @throws HotBlocksException An exception is thrown if is there is already an
	 *                            existing HotWorld for the given world (you can
	 *                            have only one HotWorld object per world).
	 * @see WorldManager WorldManager
	 */
	public HotWorld addHotWorld(World world, boolean startInPauseMode) throws HotBlocksException {
		return worldManager.addHotWorld(world, startInPauseMode);
	}

	/**
	 * Disable the HotBlocks functionality for the given world. All Modifications
	 * are aborted, all scores are deleted and the associated HotWorld objects gets
	 * invalid.
	 *
	 * @param hotWorld World where HotBlocks should be deactivated
	 * @see WorldManager WorldManager
	 */
	public void removeHotWorld(HotWorld hotWorld) {
		worldManager.removeHotWorld(hotWorld);
	}

	/**
	 * Disable the HotBlocks functionality for the given world. All Modifications
	 * are aborted, all scores are deleted and the associated HotWorld objects gets
	 * invalid.
	 *
	 * @param world World where HotBlocks should be deactivated
	 * @return TRUE true if a HotWorld was found and removed from the given world,
	 *         otherwise FALSE
	 * @see WorldManager WorldManager
	 */
	public boolean removeHotWorld(World world) {
		return worldManager.removeHotWorld(world);
	}

	/**
	 * Perform a manual check for the given block. The block is identified by its
	 * world and Coordinate. The triggerEvent is the Event which triggers the check
	 * (see listener in rules in config.yml), you can set this to null to match to
	 * any event.
	 *
	 * The check is aborted if the world is in pause-mode. If you want to ignore the
	 * pause, you should call the equivalent function in the HotWorld object.
	 *
	 * The block position is defined by the HotBlocks internal
	 * {@link de.polarwolf.hotblocks.config.Coordinate Coordinate} notation. A
	 * Location matches to the modification, if it belongs to the same word and the
	 * integer coordinates (Floor) are identical (which means they point to the same
	 * block)
	 *
	 * @param player          Player which should be checked (optional)
	 * @param world           World which should be checked
	 * @param blockCoordinate Block which should be checked. You can use
	 *                        {@link de.polarwolf.hotblocks.config.Coordinate#of(Location)
	 *                        Coordinate.of(Location)}
	 * @param triggerEvent    Event (trigger) which should be checked (optional)
	 * @return TRUE if a Modification was triggered, otherwiese FALSE
	 * @see WorldManager WorldManager
	 */
	public boolean checkBlock(Player player, World world, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		return worldManager.checkBlock(player, world, blockCoordinate, triggerEvent);
	}

	/**
	 * The same as
	 * {@link de.polarwolf.hotblocks.api.HotBlocksAPI#checkBlock(Player, World, Coordinate, TriggerEvent)
	 * checkBlock}, but the check is not performed immediately, instead scheduled
	 * for the next Minecraft Tick.
	 *
	 * @see WorldManager WorldManager
	 */
	public void checkBlockNext(Player player, World world, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		worldManager.checkBlockNext(player, world, blockCoordinate, triggerEvent);
	}

	/**
	 * Perform a manual check for the given player. This means all blocks are
	 * checked where the player is standing on.
	 *
	 * The check is aborted if the world is in pause-mode.
	 *
	 *
	 * @param player       The Player-object is only needed to check the permission
	 *                     of the rule and to award the points to him (optional)
	 * @param location     Location of the player which should be checked. Only the
	 *                     given location is checked, not the real location of the
	 *                     player-object mentioned above.
	 * @param triggerEvent Event (trigger) which should be checked (optional)
	 * @return Number of Modifications created by this check.
	 * @see WorldManager WorldManager
	 */
	public int checkPlayer(Player player, Location location, TriggerEvent triggerEvent) {
		return worldManager.checkPlayer(player, location, triggerEvent);
	}

	/**
	 * Perform a manual check for all players in the world. The check is aborted if
	 * the world is in pause-mode. If you enable HotBlcks on a world or resume from
	 * pause, only by movements the blocks are triggered. But if the player is
	 * already standing on a hot block, no action is taken. So you can use this
	 * function here to check alls blocks a player is currently standing on. The
	 * check is aborted if the world is in pause-mode.
	 *
	 * @param world World which should be checked
	 * @return Number of Modifications created by this check.
	 * @see WorldManager WorldManager
	 */
	public int checkWorld(World world) {
		return worldManager.checkWorld(world);
	}

	// Disable
	/**
	 * Check the overall HotBlocks status.
	 *
	 * @return If TRUE, the Orchestrator has already shutdown the other HotBlocks
	 *         subsystems. You cannot enable HotWorlds, add Modifications etc.
	 * @see HotBlocksOrchestrator HotBlocksOrchestrator
	 */
	public boolean isDisabled() {
		return modificationManager.isDisabled();
	}

}
