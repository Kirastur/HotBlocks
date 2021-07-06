package de.polarwolf.hotblocks.modifications;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.events.HotBlockTriggeredEvent;
import de.polarwolf.hotblocks.config.ConfigManager;

public class ModificationManager {
	
	public static final double SCAN_DEEP  = 0.05; 
	
	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected Set<Modification> modifications = new HashSet<>();
	protected Scheduler scheduler = null;
	

	public ModificationManager(Plugin plugin, ConfigManager configManager) {
		this.plugin = plugin;
		this.configManager = configManager;
	}
	
	
	// Get the complete list of of active modifications.
	// Perhaps you need it for some advanced game-features.
	public Set<Modification> getModifications() {
		return new HashSet<>(modifications);
	}
	
	
	// Get the number of currently running modifications.
	// Needed for the Scheduler to go to sleep if nothing to do.
	public int getModificationCount() {
		return modifications.size();
	}
	
	
	// Find the Modification identified by the given location.
	// A Location matchs to the modification, if
	//    it belogs to the same word
	//    the integer coordinates (Floor) are identical
	//  (which means they point to the same block)
	public Modification findModification(Location location) {
		for (Modification modification : modifications) {
			if (modification.contains(location)) {
				return modification;
			}
		}
		return null;
	}
	
	
	// Check if the given location has an active modification.
	public boolean isLocationModifying(Location location) {
		return (findModification(location) != null);
	}
	
	
	// Simply create a new Modification-object.
	// Override this for custom objects.
	protected Modification createModification(Location location, ConfigRule rule) {
		return new Modification (location.getWorld(), Coordinate.of(location), rule);
	}
	
	
	// Add a new modification for the given Location with the attached rule.
	// Starts the scheduler if needed.
	// True: The modification was successfully attached.
	// False: There is already a modification for this location. It will not be overwritten. 
	public boolean addModification(Player player, Location location, ConfigRule rule) {
		if (isLocationModifying(location)) {
			return false;
		}
		Modification newModification = createModification(location, rule);
		modifications.add(newModification);
		startScheduler();
		HotBlockTriggeredEvent myEvent = new HotBlockTriggeredEvent(player, newModification);
		plugin.getServer().getPluginManager().callEvent(myEvent);
		return true;
	}
	
	
	// Remove an existing modification.
	// True: The modification was successfully removed.
	// False: No modification for this location found.
	public boolean removeModification(Location location) {
		Modification modification = findModification(location);
		if (modification == null) {
			return false;
		}
		modifications.remove(modification);
		return true;
	}
	
	
	// Send Warn-Message to console that two rules try to modify the same location.
	protected void warnForOverlappingRules(String ruleName1, String ruleName2) {
		plugin.getLogger().warning("Overlapping rules found: " + ruleName1 + " and " + ruleName2);	
	}
	
	
	// Test if a given location fulfills the rule to get modified.
	// It is important to use the block here because we want to compare
	// the location of the block, not of the player.
	protected boolean testRuleForBlock(ConfigRule rule, Block block) {
		Location location = block.getLocation();
		return (rule.getFromMaterial().equals(block.getType()) &&
				rule.isInWorld(location) &&
				rule.isInRegion(location));
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
	
	
	// This is the core-function of the whole plugin. Please read it carefully.
	// Triggers a Modifications if all conditions are fulfilled 
	// It is dual use, you can check it with a player, or without (player is null).
	// Important: We use the given location only to find the block.
	// For all tests, the location of the block is used !!!!!
	// The player is only used to check for his permission.
	// Returns true if a modification for this location was added.
	// False means no rules apply or a modification is already active.
	public boolean checkLocation(Player player, Location location) {
		boolean hasAdd = false;
		String lastRuleName = "";
		Block block = location.getBlock();
		for (ConfigRule rule : configManager.getRules()) {
			if (testRuleForBlock(rule, block) && testRuleForPlayer(rule, player)) {
				hasAdd = addModification(player, block.getLocation(), rule) || hasAdd;
				if (!lastRuleName.isEmpty()){
					warnForOverlappingRules(lastRuleName , rule.getName());
				}
				lastRuleName = rule.getName();
			}			
		}
		return hasAdd;
	}
	
	
	// Get all Coordinates (Blocks) where the given BoundingBox is standing on.
	// Location is the place where XZ-Center of the BoundingBox is placed.
	// The high of the BoundingBox is not relevant, only the pedestal of the Box is evaluated. 
	// The filter of duplicate coordinates is done in Coordinate.equals()
	// so we don't need to care about.
	protected Set<Coordinate> calcCoordinatesToCheck(BoundingBox boundingBox, Location targetLocation) {
		Set<Coordinate> myCoordinates = new HashSet<>();

		double widthX = boundingBox.getWidthX();
		double widthZ = boundingBox.getWidthZ();
		int stepX = ((int)widthX) +1;
		int stepZ = ((int)widthZ) +1;
		double distanceX = widthX / stepX;
		double distanceZ = widthZ / stepZ;

		Location baseLocation = targetLocation.clone();
		baseLocation.add(-widthX/2 , -SCAN_DEEP, -widthZ/2);

		for (int x = 0; x <= stepX; x++) {
			for (int z = 0; z <= stepZ; z++) {
				Location myLocation = baseLocation.clone();
				myLocation.add(distanceX * x, 0, distanceZ * z);
				myCoordinates.add(Coordinate.of(myLocation));
			}
		}
		
		return myCoordinates;
	}

		
	// Check is a player is standing on top of a block.
	// Or more precise: Since the player has baseplate (XZ-rectangle of his BoundingBox),
	// he can stand on more than one block at the same time.
	// So we check all four corners of the baseplate to get the blocks below him.
	// targetLocation is the center of the player's baseplate, the player is then only used for permission check
	// Returns the number of modifications added.
	public int checkPlayer(Player player, Location targetLocation) {
		int count = 0;
		Set<Coordinate> checkCoordinates = calcCoordinatesToCheck(player.getBoundingBox(), targetLocation);
		
		for (Coordinate myCoordinate : checkCoordinates) {
			Location myLocation = myCoordinate.toLocation(targetLocation.getWorld());
			if (checkLocation(player, myLocation)) {
				count = count +1;
			}
		}
		
		return count;		
	}
	
	
	// Check all players if they stand on top of a block.
	// This is needed if you just has enabled HotBlocks for this world
	// and a player doesn't move while staying on a hot block.
	// Returns the number of modifications added.
	public int checkWorld(World world) {
		int count = 0;
		for (Player player: world.getPlayers()) {
			count = count + checkPlayer(player, player.getLocation());
		}
		return count;
	}
	
	
	// Executes the modification after the wait-time (lifetime) has expired.
	protected void endOfLifeBlock(Modification modification) {
		Location location =  modification.toLocation();
		Block block = location.getBlock();
		ConfigRule rule = modification.getRule();

		block.setType(rule.getToMaterial());
		
		if (rule.getSound() != null) {
			block.getWorld().playSound(block.getLocation(), rule.getSound(), rule.getVolume(), rule.getPitch());
		}
		
		if (rule.isContinueModify()) {
			checkLocation(null, location);
		}
	}
	

	// Called by the scheduler to reduce the lifetime of every modification
	// and executes modification if lifetime has reached zero.
	protected void decrementAndRemoveAll() {
		Set<Modification> myModifications = new HashSet<>(modifications);
		for (Modification modification : myModifications) {
			modification.decrementLifetime();
			if (modification.isEndOfLife()) {
				modifications.remove(modification);
				endOfLifeBlock(modification);
			}		
		}
	}
	
	
	// Cancel all active modifications for the given world
	// Needed for the WorldrRmove-Event
	public int cancelWorld(World world) {
		int count = 0;
		Set<Modification> myModifications = new HashSet<>(modifications);
		for (Modification modification : myModifications) {
			if (modification.getWorld().equals(world)) {
				modifications.remove(modification);
				count = count + 1;
			}		
		}
		return count;
	}
	

	// Start the Scheduler-Task if needed.
	// The Scheduler stops itself automatically if he is idle.
	protected void startScheduler() {
		if (scheduler == null) {
			scheduler = new Scheduler(this);
			scheduler.runTaskTimer(plugin, 1, 1);
		}
	}
	
	
	// Stop the scheduler
	// This is called by the scheduler itself if he detect he is idle
	protected void stopScheduler() {
		if (scheduler != null) {
			scheduler.cancel();
			scheduler = null;
		}
	}
	
	// Stop the Scheduler if the plugin gets disabled
	public void prepareDisable() {
		stopScheduler();
	}

}
