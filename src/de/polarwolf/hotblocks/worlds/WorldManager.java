package de.polarwolf.hotblocks.worlds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.api.HotBlocksAPI;
import de.polarwolf.hotblocks.api.HotBlocksOrchestrator;
import de.polarwolf.hotblocks.api.HotBlocksProvider;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.events.EventManager;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.logger.HotLogger;
import de.polarwolf.hotblocks.modifications.ModificationManager;

public class WorldManager {

	protected final Plugin plugin;
	protected final HotLogger hotLogger;
	protected final EventManager eventManager;
	protected final ModificationManager modificationManager;
	protected List<HotWorld> hotWorlds = new ArrayList<>();

	public WorldManager(HotBlocksOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.hotLogger = orchestrator.getHotLogger();
		this.eventManager = orchestrator.getEventManager();
		this.modificationManager = orchestrator.getMofificationManager();
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

	protected void prepareHotWorld(HotWorld hotWorld, HotBlocksAPI api) {
		hotWorld.setEventHelper(eventManager.getEventHelper(hotWorld));
		hotWorld.setApi(api);
	}

	public HotWorld addHotWorld(World world, boolean startInPauseMode) throws HotBlocksException {
		HotBlocksAPI api = HotBlocksProvider.getAPI();
		if (api == null) {
			throw new HotBlocksException(null, "HotBlocks API not registered, cannot add HotWorld", world.getName());
		}
		if (api.isDisabled()) {
			throw new HotBlocksException(null, "HotBlocks API is already disabled, cannot add HotWorld", world.getName());
		}
		if (hasHotWorld(world)) {
			throw new HotBlocksException(null, "HotWorld already exists, cannot add it again", world.getName());
		}
		HotWorld newHotWorld = eventManager.sendActivateWorldEvent(world, startInPauseMode);
		if (newHotWorld == null) {
			return null;
		}
		prepareHotWorld(newHotWorld, api);
		hotWorlds.add(newHotWorld);
		return newHotWorld;
	}

	public void removeHotWorld(HotWorld oldHotWorld) {
		oldHotWorld.cancel();
		modificationManager.cancelWorld(oldHotWorld.getWorld());
		hotWorlds.remove(oldHotWorld);
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

	public boolean checkBlock(Player player, World world, Coordinate blockCoordinate) {
		HotWorld hotWorld = findHotWorld(world);
		if ((hotWorld != null) && !hotWorld.isPause()) {
			return hotWorld.checkBlock(player, blockCoordinate);
		}
		return false;
	}

	public int checkPlayer(Player player, Location location) {
		int count = 0;
		World world = location.getWorld();
		HotWorld hotWorld = findHotWorld(world);
		if ((hotWorld != null) && !hotWorld.isPause()) {
			count = hotWorld.checkPlayer(player, location);
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

	public void disable() {
		for (HotWorld myHotWorld : getHotWorlds()) {
			removeHotWorld(myHotWorld);
		}
	}

}