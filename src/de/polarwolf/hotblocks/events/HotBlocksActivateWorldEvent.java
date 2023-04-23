package de.polarwolf.hotblocks.events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.worlds.HotWorld;

/**
 * This event is send when a worlds wants to get enabled for HotBlocks (e.g. by
 * "/hotblocks enable worldname"), and now we must create the appropriate
 * {@link de.polarwolf.hotblocks.worlds.HotWorld HotWorld}-object. You can
 * listen for this Event and cancel the event so that the world will not be
 * enabled for HotBlock. Or you set a custom HotWorld object. If the Event
 * doesn't return a custom HotWorld-Object, a default HotWorld is generated.
 */
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

	/**
	 * Get the world which should be enabled.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Flag if the HotWorld should be started in pause-mode.
	 */
	public boolean isStartInPauseMode() {
		return startInPauseMode;
	}

	/**
	 * Check if a previous event handler has already set a custom HotWorld-object.
	 */
	public boolean hasHotWorld() {
		return (hotWorld != null);
	}

	/**
	 * Get the HotWorld set by a previous event handler.
	 */
	public HotWorld getHotWorld() {
		return hotWorld;
	}

	/**
	 * Set the new HotWorld object. You can safely override an existing object
	 * placed here. A HotWorld objects needs to get activated before it is valid.
	 * This is done after the event has finished and no cancel occurs.
	 */
	public void setHotWorld(HotWorld newHotWorld) {
		hotWorld = newHotWorld;
	}

	/**
	 * Check if the Event is already cancelled by a previous event handler.
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Cancel the current event. The world will not be enabled for HotBlocks.
	 */
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
