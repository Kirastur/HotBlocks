package de.polarwolf.hotblocks.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.worlds.HotWorld;

public class HotBlocksCheckBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	protected final Player player;
	protected final HotWorld hotWorld;
	protected final Coordinate blockCoordinate;
	protected ConfigRule rule = null;

	HotBlocksCheckBlockEvent(Player player, HotWorld hotWorld, Coordinate blockCoordinate) {
		this.player = player;
		this.hotWorld = hotWorld;
		this.blockCoordinate = blockCoordinate;
	}

	public Player getPlayer() {
		return player;
	}

	public World getWorld() {
		return hotWorld.getWorld();
	}

	public Coordinate getBlockCoordinate() {
		return blockCoordinate;
	}

	public Location getBlockLocation() {
		return blockCoordinate.toLocation(getWorld());
	}

	public HotWorld getHotWorld() {
		return hotWorld;
	}

	public boolean hasRule() {
		return (rule != null);
	}

	public ConfigRule getRule() {
		return rule;
	}

	public void setRule(ConfigRule newRule) {
		rule = newRule;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

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
