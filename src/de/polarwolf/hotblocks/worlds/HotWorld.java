package de.polarwolf.hotblocks.worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.BoundingBox;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.config.TriggerEvent;
import de.polarwolf.hotblocks.modifications.Modification;

public class HotWorld {

	public static final double SCAN_DEEP = 0.05;

	private WorldHelper worldHelper;

	protected final World world;
	protected boolean pause;
	protected Map<UUID, Integer> scores = new HashMap<>();
	protected Objective objective;

	public HotWorld(World world, boolean pause) {
		this.world = world;
		this.pause = pause;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}

	public World getWorld() {
		return world;
	}

	protected WorldHelper getWorldHelper() {
		return worldHelper;
	}

	final void setWorldHelper(WorldHelper worldHelper) {
		this.worldHelper = worldHelper;
	}

	// Send Warn-Message to console that two rules try to modify the same location.
	protected void warnForOverlappingRules(String ruleName1, String ruleName2) {
		String s = String.format("Overlapping rules found: %s  and  %s", ruleName1, ruleName2);
		worldHelper.getHotLogger().printWarning(s);
	}

	// Test if a given location fulfills the rule to get modified.
	// It is important to use the block here because we want to compare
	// the location of the block, not of the player.
	protected boolean testRuleForBlock(ConfigRule rule, Coordinate blockCoordinate) {
		Location location = blockCoordinate.toLocation(world);
		Block block = location.getBlock();
		return (rule.hasFromMaterial(block.getType()) && rule.isInWorld(world.getName())
				&& rule.isInRegion(blockCoordinate));
	}

	// Test if the given player fulfills the rule to get the location modified.
	// If no player given, this is always true.
	protected boolean testRuleForPlayer(ConfigRule rule, Player player) {
		if (player == null) {
			return true;
		} else {
			return rule.isInPermission(player);
		}
	}

	// Test if the given TriggerEvent fulfills the rule.
	// Note: If triggerEvent is NULL this always matches.
	protected boolean testRuleForTriggerEvent(ConfigRule rule, TriggerEvent triggerEvent) {
		if (triggerEvent == null) {
			return true;
		} else {
			return rule.hasListener(triggerEvent);
		}
	}

	// Find a default rule.
	// This rule is used if the CheckBlockEvent does not deliver a custom one.
	public ConfigRule findDefaultRule(Player player, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		ConfigRule lastRule = null;
		for (ConfigRule myRule : worldHelper.getRules()) {
			if (testRuleForBlock(myRule, blockCoordinate) && testRuleForPlayer(myRule, player)
					&& testRuleForTriggerEvent(myRule, triggerEvent)) {
				if (lastRule != null) {
					warnForOverlappingRules(lastRule.getName(), myRule.getName());
				}
				lastRule = myRule;
			}
		}
		return lastRule;
	}

	// Finds the rule used to modify a block by sending a CheckBlock-Event.
	public ConfigRule findRule(Player player, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		return worldHelper.sendCheckBlockEvent(player, blockCoordinate, triggerEvent);
	}

	// Generates a default Modification.
	// This one is used if the TriggerBlock-Event does not deliver a custom one.
	public Modification generateDefaultModification(Coordinate blockCoordinate, ConfigRule rule) {
		return new Modification(this, blockCoordinate, rule);
	}

	// Create a Modification by sending a TriggerBlock-Event.
	public Modification generateModification(Player player, Coordinate blockCoordinate, ConfigRule rule) {
		return worldHelper.sendTriggerBlockEvent(player, blockCoordinate, rule);
	}

	// Add a new modification for the given Location with the attached rule.
	// True: The modification was successfully attached.
	// False: There is already a modification for this location. It will not be
	// overwritten. Or the Event was cancelled.
	public boolean triggerBlock(Player player, Coordinate blockCoordinate, ConfigRule rule) {
		if (worldHelper.isDisabled() || worldHelper.isModifying(blockCoordinate)) {
			return false;
		}
		Modification newModification = generateModification(player, blockCoordinate, rule);
		if (newModification == null) {
			return false;
		}
		worldHelper.addModification(newModification);
		if (player != null) {
			scorePlayer(player, rule);
			updateScoreboard(player, rule);
		}
		return true;
	}

	// This is the core-function of the whole plugin. Please read it carefully.
	// Triggers a Modifications if all conditions are fulfilled
	// It is dual use, you can check it with a player, or without (player is null).
	// The player is only used to check for his permission.
	// Returns true if a modification for this location was added.
	// False means no rules apply or a modification is already active.
	public boolean checkBlock(Player player, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		ConfigRule rule = findRule(player, blockCoordinate, triggerEvent);
		if (rule == null) {
			return false;
		}
		return triggerBlock(player, blockCoordinate, rule);
	}

	// Get all Coordinates (Blocks) where the given BoundingBox is standing on.
	// Location is the place where XZ-Center of the BoundingBox is placed.
	// The high of the BoundingBox is not relevant, only the pedestal of the Box is
	// evaluated.
	// The filter of duplicate coordinates is done in Coordinate.equals()
	// so we don't need to care about.
	protected Set<Coordinate> calcCoordinatesToCheck(BoundingBox boundingBox, Location targetLocation) {
		Set<Coordinate> myCoordinates = new HashSet<>();

		double widthX = boundingBox.getWidthX();
		double widthZ = boundingBox.getWidthZ();
		int stepX = ((int) widthX) + 1;
		int stepZ = ((int) widthZ) + 1;
		double distanceX = widthX / stepX;
		double distanceZ = widthZ / stepZ;

		Location baseLocation = targetLocation.clone();
		baseLocation.add(-widthX / 2, -SCAN_DEEP, -widthZ / 2);

		for (int x = 0; x <= stepX; x++) {
			for (int z = 0; z <= stepZ; z++) {
				Location myLocation = baseLocation.clone();
				myLocation.add(distanceX * x, 0, distanceZ * z);
				myCoordinates.add(Coordinate.of(myLocation));
			}
		}

		return myCoordinates;
	}

	// Check if a player is standing on top of a block.
	// Or more precise: Since the player has baseplate (XZ-rectangle of his
	// BoundingBox), he can stand on more than one block at the same time.
	// So we check all four corners of the baseplate to get the blocks below him.
	// targetLocation is the center of the player's baseplate, the player is then
	// only used for permission check
	// Returns the number of modifications added.
	public int checkPlayer(Player player, Location targetLocation, TriggerEvent triggerEvent) {
		int count = 0;
		Set<Coordinate> checkCoordinates = calcCoordinatesToCheck(player.getBoundingBox(), targetLocation);
		for (Coordinate myCoordinate : checkCoordinates) {
			if (checkBlock(player, myCoordinate, triggerEvent)) {
				count = count + 1;
			}
		}
		return count;
	}

	// Check all players if they stand on top of a block.
	// This is needed if you just has enabled HotBlocks for this world
	// and a player doesn't move while staying on a hot block.
	// Returns the number of modifications added.
	public int checkWorld() {
		int count = 0;
		for (Player player : getWorld().getPlayers()) {
			count = count + checkPlayer(player, player.getLocation(), TriggerEvent.WORLDCHECK);
		}
		return count;
	}

	protected int cancel() {
		return worldHelper.cancelModifications();
	}

	protected void scorePlayer(Player player, ConfigRule rule) {
		Integer amount = 0;
		if (scores.containsKey(player.getUniqueId())) {
			amount = scores.get(player.getUniqueId());
		}
		amount = amount + rule.getPoints();
		scores.put(player.getUniqueId(), amount);
	}

	public List<UUID> getScoredPlayers() {
		return new ArrayList<>(scores.keySet());
	}

	public int getPlayerScore(UUID playerUUID) {
		Integer amount = scores.get(playerUUID);
		if (amount == null) {
			return 0;
		} else {
			return amount;
		}
	}

	protected void updateScoreboard(Player player, ConfigRule rule) {
		try {
			if ((objective != null) && objective.isModifiable()) {
				Score objectiveScore = objective.getScore(player.getName());
				int objectiveScoreValue = objectiveScore.getScore();
				objectiveScoreValue = objectiveScoreValue + rule.getPoints();
				objectiveScore.setScore(objectiveScoreValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			objective = null;
		}
	}

}
