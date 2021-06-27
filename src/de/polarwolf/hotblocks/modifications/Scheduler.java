package de.polarwolf.hotblocks.modifications;

import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {
	
	protected ModificationManager modificationManager;
	

	public Scheduler(ModificationManager modificationManager) {
		this.modificationManager = modificationManager;
	}
	

	protected void handleTick() {
		if (modificationManager.getModificationCount() > 0) {
			modificationManager.decrementAndRemoveAll();
		} else {
			modificationManager.stopScheduler();
		}
	}
	

	@Override
    public void run() {
    	handleTick();
    }

}
