package de.polarwolf.hotblocks.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.worlds.HotWorld;

/**
 * This Event is sent when a block should be checked if there is a fitting rule
 * for it. Mostly it's sent if a player steps on a block, but it can also
 * triggered by the
 * {@link de.polarwolf.hotblocks.api.HotBlocksAPI#checkBlock(Player, World, Coordinate, TriggerEvent)
 * checkBlock} or
 * {@link de.polarwolf.hotblocks.api.HotBlocksAPI#checkPlayer(Player, Location, TriggerEvent)
 * checkPlayer} API-function.<BR>
 * The Event should return the ConfigRule object, if a matching one is found. If
 * the Event does not deliver a ConfigRule, the Systems tries to execute a
 * default algorithm to find a fitting rule. If the default algorithm also
 * doesn't find a rule, the systems assumes that there is nothing to do and the
 * block stays untouched.<BR>
 * You can cancel this event which means the internal default algorithm is not
 * executed and no modification is planned.
 */
public class HotBlocksCheckBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	protected final Player player;
	protected final HotWorld hotWorld;
	protected final Coordinate blockCoordinate;
	protected final TriggerEvent triggerEvent;
	protected ConfigRule rule = null;

	HotBlocksCheckBlockEvent(Player player, HotWorld hotWorld, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		this.player = player;
		this.hotWorld = hotWorld;
		this.blockCoordinate = blockCoordinate;
		this.triggerEvent = triggerEvent;
	}

	/**
	 * Get the player who stepped on the block. If this in null, there is no player,
	 * and the player-permission in the rule should not be checked.
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
	 * Get the HotWorld-object this Check belongs to.
	 */
	public HotWorld getHotWorld() {
		return hotWorld;
	}

	/**
	 * The triggerEvent is the Event which has triggered the check (see listener in
	 * rules). If this object is null, it should match to any Event.
	 */
	public TriggerEvent getTriggerEvent() {
		return triggerEvent;
	}

	/**
	 * Check if a previous event handler has already set a Rule.
	 */
	public boolean hasRule() {
		return (rule != null);
	}

	/**
	 * Get the Rule set by a previous event handler.
	 */
	public ConfigRule getRule() {
		return rule;
	}

	/**
	 * Set a custom Rule. This is the central function for you to hook into the
	 * check flow.
	 */
	public void setRule(ConfigRule newRule) {
		rule = newRule;
	}

	/**
	 * Check if the Event is already cancelled by a previous event handler.
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Cancel the current event. No further checks will be done and the block
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
