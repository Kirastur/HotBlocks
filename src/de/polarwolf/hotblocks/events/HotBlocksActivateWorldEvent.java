package de.polarwolf.hotblocks.events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.worlds.HotWorld;

public class HotBlocksActivateWorldEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final World world;
	private final boolean startInPauseMode;
	private boolean cancelled = false;
	private HotWorld hotWorld = null;

	HotBlocksActivateWorldEvent(World world, boolean startInPauseMode) {
		this.world = world;
		this.startInPauseMode = startInPauseMode;
	}

	public World getWorld() {
		return world;
	}

	public boolean isStartInPauseMode() {
		return startInPauseMode;
	}

	public boolean hasHotWorld() {
		return (hotWorld != null);
	}

	public HotWorld getHotWorld() {
		return hotWorld;
	}

	public void setHotWorld(HotWorld newHotWorld) {
		hotWorld = newHotWorld;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean newCancelState) {
		if (newCancelState) {
			cancelled = true;
		}
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
