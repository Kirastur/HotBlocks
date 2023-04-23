package de.polarwolf.hotblocks.modifications;

import org.bukkit.block.data.BlockData;

import de.polarwolf.hotblocks.events.EventManager;

/**
 * Providing additional functionality to the Modification-object. This is needed
 * because the Orchestrator starts the Subsystems (Managers) in a predefined
 * order, so the ModificationManager is not aware of the EventManager.
 */
public class ModificationHelper {

	protected final EventManager eventManager;
	protected final Modification modification;

	public ModificationHelper(EventManager eventManager, Modification modification) {
		this.eventManager = eventManager;
		this.modification = modification;
	}

	public boolean sendModifyBlockEvent(BlockData blockData) {
		return eventManager.sendModifyBlockEvent(modification, blockData);
	}

}
