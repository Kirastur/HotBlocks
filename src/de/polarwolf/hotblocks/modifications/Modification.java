package de.polarwolf.hotblocks.modifications;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import de.polarwolf.hotblocks.config.ConfigRule;
import de.polarwolf.hotblocks.config.Coordinate;
import de.polarwolf.hotblocks.worlds.HotWorld;

public class Modification {

	protected final HotWorld hotWorld;
	protected final Coordinate coordinate;
	protected final ConfigRule rule;
	protected int remainingLifetime;

	public Modification(HotWorld hotWorld, Coordinate coordinate, ConfigRule rule) {
		this.hotWorld = hotWorld;
		this.coordinate = coordinate;
		this.rule = rule;
		this.remainingLifetime = rule.getLifetime();
	}

	public World getWorld() {
		return hotWorld.getWorld();
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public Location getLocation() {
		return coordinate.toLocation(getWorld());
	}

	public ConfigRule getRule() {
		return rule;
	}

	public HotWorld getHotWorld() {
		return hotWorld;
	}

	public int getRemainingLifetime() {
		return remainingLifetime;
	}

	public void setRemainingLifetime(int newRemainingLifetime) {
		remainingLifetime = newRemainingLifetime;
	}

	protected void decrementRemainingLifetime() {
		if (remainingLifetime > 0) {
			remainingLifetime = remainingLifetime - 1;
		}
	}

	public boolean isEndOfLife() {
		return (remainingLifetime <= 0);
	}

	public boolean contains(World testWorld, Coordinate testCoordinate) {
		if (!getWorld().equals(testWorld)) {
			return false;
		}
		return coordinate.equals(testCoordinate);
	}

	// Perform the final Block Modification.
	// It's named "default" for standard naming convention only.
	// There is no way to change the Modification during the event.
	// The ModifyBlock-Event can only cancel.
	protected void performDefaultBlockModification() {
		Location location = getLocation();
		Block block = location.getBlock();
		block.setType(rule.getToMaterial());
		if (rule.getSound() != null) {
			block.getWorld().playSound(location, rule.getSound(), rule.getVolume(), rule.getPitch());
		}
	}

	// Prepare the Block Modification.
	// Do not call this directly because
	// it does not stop the countdown
	// so the perform can happens twice.
	protected boolean performBlockModification() {
		if (hotWorld.getEventHelper().sendModifyBlockEvent(this)) {
			performDefaultBlockModification();
			return true;
		} else {
			return false;
		}
	}

	// This function is called by the Scheduler on every Minecraft Tick
	// to decrement the remaining lifetime
	// and perform the action if EOL is reached.
	// There are two EOL checks, because the ModifyBlock-Event can change it.
	protected boolean handleTick() {
		decrementRemainingLifetime();
		if (!isEndOfLife()) {
			return false;
		}
		if (!performBlockModification()) {
			return false;
		}
		if (!isEndOfLife()) {
			return false;
		}
		return rule.isContinueModify();
	}

}
