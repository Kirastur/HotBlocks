package de.polarwolf.hotblocks.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HotBlocksReloadRequestEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	protected final CommandSender initiator;
	protected boolean acknowledged = false;

	HotBlocksReloadRequestEvent(CommandSender initiator) {
		this.initiator = initiator;
	}

	public CommandSender getInitiator() {
		return initiator;
	}

	public boolean getAcknowledged() {
		return acknowledged;
	}

	public void acknowledge() {
		acknowledged = true;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
