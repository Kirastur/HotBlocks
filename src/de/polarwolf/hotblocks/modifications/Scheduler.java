package de.polarwolf.hotblocks.modifications;

import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {

	protected final ModificationManager modificationManager;

	public Scheduler(ModificationManager modificationManager) {
		this.modificationManager = modificationManager;
	}

	@Override
	public void run() {
		try {
			modificationManager.handleTick();
		} catch (Exception e) {
			e.printStackTrace();
			cancel();
		}
	}

}
