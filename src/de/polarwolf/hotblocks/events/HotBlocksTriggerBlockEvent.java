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

	public ConfigRule getRule() {
		return rule;
	}

	public HotWorld getHotWorld() {
		return hotWorld;
	}

	public boolean hasModification() {
		return (modification != null);
	}

	public Modification getModification() {
		return modification;
	}

	public void setModification(Modification newModification) {
		modification = newModification;
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
