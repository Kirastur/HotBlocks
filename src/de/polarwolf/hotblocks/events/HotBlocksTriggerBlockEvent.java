package de.polarwolf.hotblocks.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.worlds.HotWorld;

/**
 * After the HotBlocks system has detected that a block has a matching rule
 * (e.g. the player has walked over a block and that blocks matches a rule),
 * this block is scheduled to be modified or removed. This scheduler in
 * HotBlocks is represented by a
 * {@link de.polarwolf.hotblocks.modifications.Modification Modification}.<BR>
 * The HotBlocksTriggerBlockEvent is sent to create the needed Modification. If
 * the Event doesn't deliver a Notification-object, the system uses the default
 * Modification-object. You can hook into the Event to perform the following
 * actions:
 * <UL>
 * <LI>Cancel the event, so no modification is done.</LI>
 * <LI>Set a custom Notification object (e.g. with special treatment).</LI>
 * <LI>Update counters and statistics (use EventPriority=MONITOR).</LI>
 * </UL>
 * Except you have done some obscure customization in your custom
 * HotWorld-object, you can rely that a Modification is started if the Event was
 * not cancelled.
 */
public class HotBlocksTriggerBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	protected final Player player;
	protected final HotWorld hotWorld;
	protected final Coordinate blockCoordinate;
	protected final ConfigRule rule;
	protected Modification modification = null;

	HotBlocksTriggerBlockEvent(Player player, HotWorld hotWorld, Coordinate blockCoordinate, ConfigRule rule) {
		this.player = player;
		this.hotWorld = hotWorld;
		this.blockCoordinate = blockCoordinate;
		this.rule = rule;
	}

	/**
	 * Get the player who steped on the block.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the world the target block is in.
	 */
	public World getWorld() {
		return hotWorld.getWorld();
	}

	/**
	 * Get the coordinates of the target block.
	 */
	public Coordinate getBlockCoordinate() {
		return blockCoordinate;
	}

	/**
	 * A simple supporter function, takes the world and the coordinates and builds a
	 * Minecraft Location-object from it.
	 */
	public Location getBlockLocation() {
		return blockCoordinate.toLocation(getWorld());
	}

	/**
	 * Get the rule which should be applied.
	 */
	public ConfigRule getRule() {
		return rule;
	}

	/**
	 * Get the HotWorld-object this Modification belongs to.
	 */
	public HotWorld getHotWorld() {
		return hotWorld;
	}

	/**
	 * Check if a previous event handler has already set a Modification.
	 */
	public boolean hasModification() {
		return (modification != null);
	}

	/**
	 * Get the Modification set by a previous event handler.
	 */
	public Modification getModification() {
		return modification;
	}

	/**
	 * Set a custom Modification. This is the central function for you to hook into
	 * the Trigger flow.
	 */
	public void setModification(Modification newModification) {
		modification = newModification;
	}

	/**
	 * Check if the Event is already cancelled by a previous event handler.
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Cancel the current event. No further trigger will be done and the block
	 * remains unchanged.
	 */
	@Override
	public void setCancelled(boolean newCancelState) {
		cancelled = newCancelState;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
