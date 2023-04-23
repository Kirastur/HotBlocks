package de.polarwolf.hotblocks.events;

import org.bukkit.block.data.BlockData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.modifications.Modification;

/**
 * After a Modification was triggered, and the lifetime counter of this
 * Modification has reached zero, the block must be changed or removed now. This
 * is the last Event in the flow.<BR>
 * This Event is the last chance to cancel the modification. No other options
 * than canceling the Event is allowed by your Event Handler. A cancel stops the
 * Modification from being executed.<BR>
 * <B>Hint</B>: The execution itself is part of the Modification-Object. For a
 * custom action you must change the Modification-object and hook into the
 * {@link de.polarwolf.hotblocks.events.HotBlocksTriggerBlockEvent
 * HotBlocksTriggerBlockEvent}.
 */
public class HotBlocksModifyBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	protected Modification modification;
	protected BlockData blockData;

	HotBlocksModifyBlockEvent(Modification modification, BlockData blockData) {
		this.modification = modification;
		this.blockData = blockData;
	}

	/**
	 * Get the Modification. All other information about the Event can be taken from
	 * the Modification-Object.
	 */
	public Modification getModification() {
		return modification;
	}

	/**
	 * Get the designated BlockData for the toMaterial. You can change some
	 * material-specific settings here in the event, but you are not allowed to
	 * change the material itself.
	 */
	public BlockData getBlockData() {
		return blockData;
	}

	/**
	 * Check if the Event is already cancelled by a previous event handler.
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Cancel the current event, so the block remains unchanged.
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
