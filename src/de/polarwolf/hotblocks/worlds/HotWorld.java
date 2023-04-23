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

/**
 * A HotWorld objects represents the HotBlocks monitor for a specific world. You
 * enable a world for HotBlocks by creating the appropriate HotWorld object (via
 * the addHotWorld API-function). The deactivation is done by removeHotWorld. If
 * you need an existing HotWorld object for a specific word, just call
 * findHotWorld (see WorldManager).
 */
public class HotWorld {

	/**
	 * Distance below the player's feet to check for the block. This is a quick fix,
	 * because in a previous version of minecraft after a jump you landed a bit
	 * above the block surface.
	 */
	public static final double SCAN_DEEP = 0.05;

	/**
	 * Shortens the size of the player's bounding box. This is a quick fix, because
	 * of a math rounding issue the player can trigger the modification when he
	 * jumps next to a hotblock.
	 */
	public static final double BOUNDINGBOX_BORDER = 0.0001;

	private WorldHelper worldHelper;

	protected final World world;
	protected boolean pause;
	protected Map<UUID, Integer> scores = new HashMap<>();
	protected Objective objective;

	/**
	 * Creates a new HotWorld object. You can optionally set the word in pause-mode
	 * on start. A HotWorld object is not usable until it was registered to the
	 * WorldManager. The only way to register this object is to call WorldManager's
	 * addWorld, and handle the HotBlocksActivateWorldEvent.
	 *
	 * @param world The world the HotWorld should created for
	 * @param pause TRUE if the HotWorld should start in PAUSE-Mode, otherwise
	 *              FALSE.
	 */
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

	/**
	 * You can link the internal score to a Minecraft Scoreboard objective. With
	 * this function you can query the current link.
	 */
	public Objective getObjective() {
		return objective;
	}

	/**
	 * You can link the internal score to a Minecraft Scoreboard objective. This
	 * means, every time the player collects internal points by triggering a
	 * modification, the same amount of points is added to the objective. The
	 * objective is not resetted nor the points are removed if the player leaves the
	 * HotWorld or the HotWorld gets removed. This handling is in your
	 * responsibility. HotBlocks simply add points.
	 */
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

	/**
	 * Send Warn-Message to console that two rules try to modify the same location.
	 */
	protected void warnForOverlappingRules(String ruleName1, String ruleName2) {
		String s = String.format("Overlapping rules found: %s  and  %s", ruleName1, ruleName2);
		worldHelper.getHotLogger().printWarning(s);
	}

	/**
	 * Test if a given location fulfills the rule to get modified. It is important
	 * to use the block here because we want to compare the location of the block,
	 * not of the player.
	 */
	protected boolean testRuleForBlock(ConfigRule rule, Coordinate blockCoordinate) {
		Location location = blockCoordinate.toLocation(world);
		Block block = location.getBlock();
		return (rule.hasFromMaterial(block.getType()) && rule.isInWorld(world.getName())
				&& rule.isInRegion(blockCoordinate));
	}

	/**
	 * Test if the given player fulfills the rule to get the location modified. If
	 * no player given, this is always true.
	 */
	protected boolean testRuleForPlayer(ConfigRule rule, Player player) {
		if (player == null) {
			return true;
		} else {
			return rule.isInPermission(player);
		}
	}

	/**
	 * Test if the given TriggerEvent fulfills the rule. Note: If triggerEvent is
	 * NULL this always matches.
	 */
	protected boolean testRuleForTriggerEvent(ConfigRule rule, TriggerEvent triggerEvent) {
		if (triggerEvent == null) {
			return true;
		} else {
			return rule.hasListener(triggerEvent);
		}
	}

	/**
	 * Find a default rule. This rule is used if the CheckBlockEvent does not
	 * deliver a custom one.
	 */
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

	/**
	 * Find a matching rule. For this a HotBlocksCheckBlockEvent is called. The
	 * ConfigRule received here can be used to call the triggerBlock function to
	 * start a Modification with the this rule.
	 */
	public ConfigRule findRule(Player player, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		return worldHelper.sendCheckBlockEvent(player, blockCoordinate, triggerEvent);
	}

	/**
	 * Generates a default Modification. This one is used if the TriggerBlock-Event
	 * does not deliver a custom one.
	 */
	public Modification generateDefaultModification(Coordinate blockCoordinate, ConfigRule rule) {
		return new Modification(this, blockCoordinate, rule);
	}

	/**
	 * Generate a Modification object, which can be later used in the API-function
	 * addModification to start a Modification. The generation is done by sending a
	 * HotBlockTriggerBlockEvent. (The triggerBlock is just a combination of
	 * generateModification and addModification and point awarding.)
	 */
	public Modification generateModification(Player player, Coordinate blockCoordinate, ConfigRule rule) {
		return worldHelper.sendTriggerBlockEvent(player, blockCoordinate, rule);
	}

	/**
	 * With this function you can start a modification. There is no check done
	 * before, the player is only used to grant the points to him, and can be null.
	 * The function can fail if there is already an existing modification for this
	 * block (a block can have only one modification at a time), or the
	 * HotBlocksTriggerBlockEvent has cancelled the action. In these cases, false is
	 * returned, otherwise true. Pause-Mode is ignored.
	 */
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

	/**
	 * With this function you can perform a check for a given block. The function
	 * cycles through all Rules and tries to find a matching rule for the player
	 * (player permission), block (material, region etc.) and the Event which
	 * triggers the check (see listener in rules config.yml). If a matching rule is
	 * found, the Modification is triggered and true is returned, otherwise false.
	 * Pause-Mode is ignored. (You can set triggerEvent to null to match to any
	 * event.)
	 *
	 * It is dual use, you can check it with a player, or without (player is null).
	 * The player is only used to check for his permission. Returns true if a
	 * modification for this location was added. False means no rules apply or a
	 * modification is already active.
	 */
	public boolean checkBlock(Player player, Coordinate blockCoordinate, TriggerEvent triggerEvent) {
		ConfigRule rule = findRule(player, blockCoordinate, triggerEvent);
		if (rule == null) {
			return false;
		}
		return triggerBlock(player, blockCoordinate, rule);
	}

	/**
	 * Get all Coordinates (Blocks) where the given BoundingBox is standing on.
	 * Location is the place where XZ-Center of the BoundingBox is placed. The high
	 * of the BoundingBox is not relevant, only the pedestal of the Box is
	 * evaluated. The filter of duplicate coordinates is done in Coordinate.equals()
	 * so we don't need to care about.
	 */
	protected Set<Coordinate> calcCoordinatesToCheck(BoundingBox boundingBox, Location targetLocation) {
		Set<Coordinate> myCoordinates = new HashSet<>();

		double widthX = boundingBox.getWidthX() - BOUNDINGBOX_BORDER;
		double widthZ = boundingBox.getWidthZ() - BOUNDINGBOX_BORDER;
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

	/**
	 * Check if a player is standing on top of a block. Or more precise: Since the
	 * player has baseplate (XZ-rectangle of his BoundingBox), he can stand on more
	 * than one block at the same time. So we check all four corners of the
	 * baseplate to get the blocks below him. targetLocation is the center of the
	 * player's baseplate, the player is then only used for permission check Returns
	 * the number of modifications added.
	 */
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

	/**
	 * Check all players if they stand on top of a block. This is needed if you just
	 * has enabled HotBlocks for this world and a player doesn't move while staying
	 * on a hot block. Returns the number of modifications added.
	 */
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

	/**
	 * If you define a "point" property in the Rules configuration, the player is
	 * awarded with points if he triggers a modification. The points are counted
	 * internally. With this function you can get a list of players which have
	 * received points in the current world. You can get the Name of the player by
	 * calling Bukkit.findOfflinePlayer(UUID).getName().
	 */
	public List<UUID> getScoredPlayers() {
		return new ArrayList<>(scores.keySet());
	}

	/**
	 * Get the current player score in this world. If the player was not found or
	 * has not achieved any points, zero is returned.
	 */
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
