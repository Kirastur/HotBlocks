package de.polarwolf.hotblocks.config;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ReloadScheduler extends BukkitRunnable {

	protected final ConfigManager configManager;

	public ReloadScheduler(Plugin plugin, ConfigManager configManager) {
		this.configManager = configManager;
		runTask(plugin);
	}

	@Override
	public void run() {
		try {
			configManager.reload(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
