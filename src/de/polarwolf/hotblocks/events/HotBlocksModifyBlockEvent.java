package de.polarwolf.hotblocks.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.modifications.Modification;

public class HotBlocksModifyBlockEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	protected Modification modification;

	HotBlocksModifyBlockEvent(Modification modification) {
		this.modification = modification;
	}

	public Modification getModification() {
		return modification;
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
