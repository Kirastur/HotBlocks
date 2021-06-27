package de.polarwolf.hotblocks.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.polarwolf.hotblocks.exception.HotBlocksException;

public class ConfigRule {
	
	
	protected final String name;
	protected final Pattern worlds;
	protected final Region region;
	protected final String permission;
	protected final Material fromMaterial;
	protected final Material toMaterial;
	protected final int lifetime;
	protected final Sound sound;
	protected final float volume;
	protected final float pitch;
	protected final boolean continueModify;
	

	public ConfigRule(String name, Map<String,String> parameters) throws HotBlocksException {
		this.name = name;
		verifyMap(parameters);

		Coordinate corner1 = loadCoordinateValue(ConfigParam.CORNER1, parameters);
		Coordinate corner2 = loadCoordinateValue(ConfigParam.CORNER2, parameters);
		this.region = buildRegion(corner1, corner2);
		
		this.fromMaterial = loadMaterialValue(ConfigParam.FROM_MATERIAL, parameters);
		this.toMaterial = loadMaterialValue(ConfigParam.TO_MATERIAL, parameters);
		if ((fromMaterial == null) || (toMaterial == null)) {
			throw new HotBlocksException(getName(), "Materials must be defined", null);			
		}

		this.lifetime = loadIntValue(ConfigParam.LIFETIME, parameters);
		if (lifetime < 1) {
			throw new HotBlocksException(getName()+"." + ConfigParam.LIFETIME.getAttributeName(), "Illegal Lifetime", Integer.toString(lifetime));
		}

		this.worlds = loadWorldRegexValue(ConfigParam.WORLDS, parameters);
		this.permission = loadStringValue(ConfigParam.PERMISSION, parameters);
		this.sound = loadSoundValue(ConfigParam.SOUND, parameters);
		this.volume = loadFloatValue(ConfigParam.VOLUME, parameters);
		this.pitch = loadFloatValue(ConfigParam.PITCH, parameters);
		this.continueModify = loadBoolValue(ConfigParam.CONTINUE_MODIFY, parameters);
	}


	public String getName() {
		return name;
	}
	

	public Pattern getWorlds() {
		return worlds;
	}


	public Region getRegion() {
		return region;
	}


	public String getPermission() {
		return permission;
	}


	public Material getFromMaterial() {
		return fromMaterial;
	}


	public Material getToMaterial() {
		return toMaterial;
	}


	public int getLifetime() {
		return lifetime;
	}


	public Sound getSound() {
		return sound;
	}


	public float getVolume() {
		return volume;
	}


	public float getPitch() {
		return pitch;
	}


	public boolean isContinueModify() {
		return continueModify;
	}
	

	protected boolean isAttribute(String attributeName) {
		List<ConfigParam> configParams = Arrays.asList(ConfigParam.values());
		for (ConfigParam configParam : configParams) {
			if (configParam.getAttributeName().equalsIgnoreCase(attributeName)) {
				return true;
			}
		}
		return false;
		
	}
	

	protected void verifyMap (Map<String,String> parameters) throws HotBlocksException {
		for (String attributeName : parameters.keySet()) {
			if (!isAttribute(attributeName)) {
				throw new HotBlocksException (getName(), "Unknown Attribute", attributeName);
			}
		}		
	}
	
	
	protected String loadStringValue(ConfigParam attribute, Map<String,String> parameters) {
		for (Entry<String,String> entry : parameters.entrySet()) {
			if (attribute.getAttributeName().equalsIgnoreCase(entry.getKey())) {
				String value = entry.getValue();
				if (value != null) {
					return value;
				}
			}
		}
		return attribute.getDefaultValue();
	}
	
	
	protected int loadIntValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Value is not integer", value);
		}
	}
		

	protected float loadFloatValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		try {
			return Float.parseFloat(value); 
		} catch (Exception e) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Value is not numeric", value);
		}
	}

	
	protected boolean loadBoolValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if ((value.equalsIgnoreCase("TRUE")) || (value.equalsIgnoreCase("YES"))) {
			return true;
		}
		if ((value.equalsIgnoreCase("FALSE")) || (value.equalsIgnoreCase("NO"))) {
			return false;
		}
		throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Value is not boolean", value);
	}
	
	
	protected Material loadMaterialValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		try {
			return Material.getMaterial(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Unknown Material", value);
		}
	}


	protected Sound loadSoundValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		try {
			return Sound.valueOf(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Unknown Sound", value);
		}
	}
	

	protected Coordinate loadCoordinateValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		String[] coordinates = value.split(";");
		if (coordinates.length != 3) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Not a coordinate format", value);
		}
		try {
			int x = Integer.parseInt(coordinates[0]);
			int y = Integer.parseInt(coordinates[1]);
			int z = Integer.parseInt(coordinates[2]);
			return new Coordinate (x, y, z);  			
		} catch (Exception e) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Not a coordinate format", value);
		}
	}
	

	protected Pattern loadWorldRegexValue(ConfigParam attribute, Map<String,String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		if (!value.startsWith("^")) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Word Regex should start with \"^\"", value);
		}
		try {
			return Pattern.compile(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName()+"."+attribute.getAttributeName(), "Value is not RegEx", value);
		}
	}
	
	
	protected Region buildRegion (Coordinate corner1, Coordinate corner2) throws HotBlocksException {
		if ((corner1 == null) ^ (corner2 == null)) {
			throw new HotBlocksException(getName(), "Second coordinate is missing", null);
		}
		if (corner1 == null) {
			return null;
		}
		return new Region(corner1, corner2);
	}
	

	public boolean isInWorld(Location loc) {
		if (worlds == null) {
			return true;			
		}
		String worldName = loc.getWorld().getName();
		Matcher matcher = worlds.matcher(worldName);
		return matcher.matches();
	}
	
	
	public boolean isInRegion(Location location) {
		if (region == null) {
			return true;
		}
		return region.contains(Coordinate.of(location));  
	}
	
	
	public boolean isInPermission(Player player) {
		if (permission.isEmpty()) {
			return true;
		}
		return player.hasPermission(permission);
	}
	
}	
