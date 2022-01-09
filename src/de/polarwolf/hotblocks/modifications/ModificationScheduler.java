package de.polarwolf.hotblocks.modifications;

import org.bukkit.scheduler.BukkitRunnable;

public class ModificationScheduler extends BukkitRunnable {

	protected final ModificationManager modificationManager;

	public ModificationScheduler(ModificationManager modificationManager) {
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
