package de.polarwolf.hotblocks.config;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.hotblocks.worlds.WorldManager;

public class ReloadScheduler extends BukkitRunnable {

	protected final ConfigManager configManager;
	protected final WorldManager worldManager;
	protected final CommandSender initiator;

	public ReloadScheduler(Plugin plugin, ConfigManager configManager, WorldManager worldManager,
			CommandSender initiator) {
		this.configManager = configManager;
		this.worldManager = worldManager;
		this.initiator = initiator;
		runTask(plugin);
	}

	@Override
	public void run() {
		try {
			configManager.reload(worldManager, initiator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
