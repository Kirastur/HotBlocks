package de.polarwolf.hotblocks.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.hotblocks.config.ConfigManager;
import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.ConfigSection;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.modifications.Modification;
import de.polarwolf.hotblocks.modifications.ModificationManager;
import de.polarwolf.hotblocks.worlds.HotWorld;
import de.polarwolf.hotblocks.worlds.WorldManager;

public class HotBlocksAPI {

	private final ConfigManager configManager;
	private final ModificationManager modificationManager;
	private final WorldManager worldManager;
	private final HotBlocksOrchestrator orchestrator;

	public HotBlocksAPI(HotBlocksOrchestrator orchestrator) {
		this.configManager = orchestrator.getConfigManager();
		this.modificationManager = orchestrator.getMofificationManager();
		this.worldManager = orchestrator.getWorldManager();
		this.orchestrator = orchestrator;
	}

	// ConfigManager
	public List<ConfigRule> getRules() {
		return configManager.getRules();
	}

	public ConfigSection buildConfigSectionFromLocalConfigFile(Plugin plugin, String configPath)
			throws HotBlocksException {
		return configManager.buildConfigSectionFromLocalConfigFile(plugin, configPath);
	}

	public void replaceConfig(ConfigSection newConfigSection, CommandSender initiator) {
		configManager.replaceConfig(newConfigSection, initiator);
	}

	public void reload(CommandSender initiator) {
		configManager.reload(initiator);
	}

	public void scheduleRedloadFoNextTick() {
		configManager.scheduleRedloadFoNextTick();
	}

	// ModificationManager
	public List<Modification> getModifications() {
		return modificationManager.getModifications();
	}

	public Modification findModification(World world, Coordinate blockCoordinate) {
		return modificationManager.findModification(world, blockCoordinate);
	}

	public boolean isModifying(World world, Coordinate blockCoordinate) {
		return modificationManager.isModifying(world, blockCoordinate);
	}

	public void addModification(Modification newModification) {
		modificationManager.addModification(newModification);
	}

	public boolean removeModification(Modification oldModification) {
		return modificationManager.removeModification(oldModification);
	}

	public int cancelModifications(World world) {
		return modificationManager.cancelWorld(world);
	}

	// WorldManager
	public List<HotWorld> getHotWorlds() {
		return worldManager.getHotWorlds();
	}

	public HotWorld findHotWorld(World world) {
		return worldManager.findHotWorld(world);
	}

	public boolean hasHotWorld(World world) {
		return worldManager.hasHotWorld(world);
	}

	public HotWorld addHotWorld(World world, boolean startInPauseMode) throws HotBlocksException {
		return worldManager.addHotWorld(world, startInPauseMode);
	}

	public void removeHotWorld(HotWorld hotWorld) {
		worldManager.removeHotWorld(hotWorld);
	}

	public boolean removeHotWorld(World world) {
		return worldManager.removeHotWorld(world);
	}

	public boolean checkBlock(Player player, World world, Coordinate blockCoordinate) {
		return worldManager.checkBlock(player, world, blockCoordinate);
	}

	public int checkPlayer(Player player, Location location) {
		return worldManager.checkPlayer(player, location);
	}

	public int checkWorld(World world) {
		return worldManager.checkWorld(world);
	}

	// Disable
	public boolean isDisabled() {
		return orchestrator.isDisabled();
	}

}
