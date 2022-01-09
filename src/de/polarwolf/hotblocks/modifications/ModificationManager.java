package de.polarwolf.hotblocks.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.events.EventManager;
import de.polarwolf.hotblocks.logger.HotLogger;

public class ModificationManager {

	protected final Plugin plugin;
	protected final HotLogger hotLogger;
	protected final EventManager eventManager;

	private boolean disabled = false;
	protected List<Modification> modifications = new ArrayList<>();
	protected ModificationScheduler modificationScheduler = null;

	public ModificationManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.hotLogger = orchestrator.getHotLogger();
		this.eventManager = orchestrator.getEventManager();
	}

	// Get the complete list of of active modifications.
	// Perhaps you need it for some advanced game-features.
	public List<Modification> getModifications() {
		return new ArrayList<>(modifications);
	}

	// Test if ModificationQueue is empty
	// Needed for the Scheduler to go to sleep if nothing to do.
	public boolean isModificationQueueEmpty() {
		return modifications.isEmpty();
	}

	// Find the Modification identified by the given location.
	// A Location matches to the modification, if
	// it belongs to the same word and
	// the integer coordinates (Floor) are identical
	// (which means they point to the same block)
	public Modification findModification(World world, Coordinate blockCoordinate) {
		for (Modification myModification : modifications) {
			if (myModification.contains(world, blockCoordinate)) {
				return myModification;
			}
		}
		return null;
	}

	// Check if the given location has an active modification.
	public boolean isModifying(World world, Coordinate blockCoordinate) {
		return (findModification(world, blockCoordinate) != null);
	}

	// A modification us useless until it is registered to the ModificationManager.
	// During this process, the Modification gets a gateway to other Managers.
	protected void prepareModification(Modification modification) {
		modification.setModificationHelper(new ModificationHelper(eventManager, modification));
	}

	// Add an existing modification to the central list,
	// so the scheduler can decrement lifetime and perform action on EOL.
	// This function does not check if there is already another
	// modification for the same coordinates.
	// So use this function carefully.
	public void addModification(Modification newModification) {
		if (!disabled) {
			prepareModification(newModification);
			modifications.add(newModification);
			hotLogger.printDebug(String.format("New Modification with Rule \"%s\" in world \"%s\" added",
					newModification.getRule().getName(), newModification.getWorld().getName()));
			startScheduler();
		}
	}

	// Remove an existing modification.
	// This stops the lifetime countdown.
	// True: The modification was successfully removed.
	// False: No modification for this location found.
	public boolean removeModification(Modification oldModification) {
		if (modifications.contains(oldModification)) {
			modifications.remove(oldModification);
			hotLogger.printDebug(String.format("Modification with Rule \"%s\" in world \"%s\" removed",
					oldModification.getRule().getName(), oldModification.getWorld().getName()));
			return true;
		} else {
			return false;
		}
	}

	// Called by the scheduler to reduce the lifetime of every modification.
	// Important: The continueModify-Check is done AFTER the modification
	// was removed from queue, so that the duplicate check is not preventing
	// the follow-on modification
	protected void handleTick() {
		if (isModificationQueueEmpty()) {
			stopScheduler();
			return;
		}
		for (Modification myModification : new ArrayList<>(modifications)) {
			boolean continueModify = myModification.handleTick();
			if (myModification.isEndOfLife()) {
				removeModification(myModification);
				if (continueModify) {
					Coordinate blockCoordinate = myModification.getCoordinate();
					myModification.getHotWorld().checkBlock(null, blockCoordinate, TriggerEvent.CASCADE);
				}
			}
		}
	}

	// Cancel all active modifications for the given world
	// Needed for the WorldUnload-Event
	public int cancelWorld(World world) {
		int count = 0;
		for (Modification myModification : new ArrayList<>(modifications)) {
			if (myModification.getWorld().equals(world)) {
				removeModification(myModification);
				count = count + 1;
			}
		}
		hotLogger.printDebug(String.format("Deleted %d Modifications in world \"%s\"", count, world.getName()));
		return count;
	}

	// Start the Scheduler-Task if needed.
	// The Scheduler stops itself automatically if he is idle.
	protected void startScheduler() {
		if (modificationScheduler == null) {
			modificationScheduler = new ModificationScheduler(this);
			modificationScheduler.runTaskTimer(plugin, 1, 1);
			hotLogger.printDebug("Modification scheduler started");
		}
	}

	// Stop the scheduler
	// This is called if ModificationList is empty
	protected void stopScheduler() {
		if (modificationScheduler != null) {
			modificationScheduler.cancel();
			modificationScheduler = null;
			hotLogger.printDebug("Modification scheduler stopped");
		}
	}

	public boolean isDisabled() {
		return disabled;
	}

	// Stop the Scheduler if the plugin gets disabled
	public void disable() {
		disabled = true;
		stopScheduler();
		modifications.clear();
	}

}
