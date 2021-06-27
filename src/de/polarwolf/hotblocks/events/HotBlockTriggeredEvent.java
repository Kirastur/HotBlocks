package de.polarwolf.hotblocks.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.hotblocks.modifications.Modification;

public class HotBlockTriggeredEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	protected final Player player;
	protected final Modification modification;
	
	
	public HotBlockTriggeredEvent(Player player, Modification modification) {
		this.player = player;
		this.modification = modification;
	}


	public Player getPlayer() {
		return player;
	}


	public Modification getModification() {
		return modification;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	

	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
