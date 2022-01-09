package de.polarwolf.hotblocks.worlds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.events.EventManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.listener.ListenManager;
import de.polarwolf.hotblocks.logger.HotLogger;
import de.polarwolf.hotblocks.modifications.ModificationManager;

public class WorldManager {

	protected final Plugin plugin;
	protected final HotLogger hotLogger;
	protected final EventManager eventManager;
	protected final ConfigManager configManager;
	protected final ListenManager listenManager;
	protected final ModificationManager modificationManager;

	protected List<HotWorld> hotWorlds = new ArrayList<>();

	public WorldManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.hotLogger = orchestrator.getHotLogger();
		this.eventManager = orchestrator.getEventManager();
		this.configManager = orchestrator.getConfigManager();
		this.listenManager = orchestrator.getListenManager();
		this.modificationManager = orchestrator.getModificationManager();
	}

	public List<HotWorld> getHotWorlds() {
		return new ArrayList<>(hotWorlds);
	}

	public HotWorld findHotWorld(World world) {
		for (HotWorld myHotWorld : hotWorlds) {
			if (myHotWorld.getWorld().equals(world)) {
				return myHotWorld;
			}
		}
		return null;
	}

	public boolean hasHotWorld(World world) {
		return (findHotWorld(world) != null);
	}

	protected void prepareHotWorld(HotWorld hotWorld) {
		hotWorld.setWorldHelper(new WorldHelper(hotLogger, eventManager, configManager, modificationManager, hotWorld));
	}

	public void updateListener() {
		listenManager.updateListener(this);
	}

	public HotWorld addHotWorld(World world, boolean startInPauseMode) throws HotBlocksException {
		if (modificationManager.isDisabled()) {
			throw new HotBlocksException(null, "HotBlocks engine is disabled, cannot add HotWorld", world.getName());
		}
		if (hasHotWorld(world)) {
			throw new HotBlocksException(null, "HotWorld already exists, cannot add it again", world.getName());
		}
		HotWorld newHotWorld = eventManager.sendActivateWorldEvent(world, startInPauseMode);
		if (newHotWorld == null) {
			return null;
		}
		prepareHotWorld(newHotWorld);
		hotWorlds.add(newHotWorld);
		updateListener();
		return newHotWorld;
	}

	public void removeHotWorld(HotWorld oldHotWorld) {
		oldHotWorld.cancel();
		hotWorlds.remove(oldHotWorld);
		updateListener();
		hotLogger.printDebug(String.format("Disabling HotBlocks in world \"%s\"", oldHotWorld.getWorld().getName()));
	}

	public boolean removeHotWorld(World oldWorld) {
		HotWorld oldHotWorld = findHotWorld(oldWorld);
		if (oldHotWorld != null) {
			removeHotWorld(oldHotWorld);
			return true;
		}
		return false;
	}

	public boolean checkBlock(Player player, World world, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		HotWorld hotWorld = findHotWorld(world);
		if ((hotWorld != null) && !hotWorld.isPause()) {
			return hotWorld.checkBlock(player, blockCoordinate, triggerEvent);
		}
		return false;
	}

	public int checkPlayer(Player player, Location location, TriggerEvent triggerEvent) {
		int count = 0;
		World world = location.getWorld();
		HotWorld hotWorld = findHotWorld(world);
		if ((hotWorld != null) && !hotWorld.isPause()) {
			count = hotWorld.checkPlayer(player, location, triggerEvent);
		}
		return count;
	}

	public int checkWorld(World world) {
		int count = 0;
		HotWorld hotWorld = findHotWorld(world);
		if ((hotWorld != null) && !hotWorld.isPause()) {
			count = hotWorld.checkWorld();
		}
		hotLogger.printDebug(String.format("Created %d Modifications in world \"%s\"", count, world.getName()));
		return count;
	}

	public void checkBlockNext(Player player, World world, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		new CheckBlockScheduler(plugin, this, player, world, blockCoordinate, triggerEvent);
	}

	public void disable() {
		for (HotWorld myHotWorld : getHotWorlds()) {
			removeHotWorld(myHotWorld);
		}
	}

}