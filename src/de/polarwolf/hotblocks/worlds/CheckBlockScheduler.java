package de.polarwolf.hotblocks.worlds;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;

/**
 * Internal Scheduler for checkBlockNext
 */
public class CheckBlockScheduler extends BukkitRunnable {

	protected final WorldManager worldManager;
	protected final Player player;
	protected final World world;
	protected final Coordinate blockCoordinate;
	protected final TriggerEvent triggerEvent;

	public CheckBlockScheduler(Plugin plugin, WorldManager worldManager, Player player, World world,
			Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		this.worldManager = worldManager;
		this.player = player;
		this.world = world;
		this.blockCoordinate = blockCoordinate;
		this.triggerEvent = triggerEvent;
		this.runTask(plugin);
	}

	@Override
	public void run() {
		try {
			worldManager.checkBlock(player, world, blockCoordinate, triggerEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
