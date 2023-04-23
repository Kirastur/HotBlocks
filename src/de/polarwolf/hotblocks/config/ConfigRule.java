package de.polarwolf.hotblocks.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.polarwolf.hotblocks.exception.HotBlocksException;

/**
 * A rule is a set of conditions and actions which defines a modification.
 */
public class ConfigRule {

	protected final String name;
	protected Pattern worlds;
	protected Region region;
	protected String permission;
	protected Set<Material> fromMaterials = new HashSet<>();
	protected Material toMaterial;
	protected int lifetime;
	protected Sound sound;
	protected float volume;
	protected float pitch;
	protected boolean continueModify;
	protected int points;
	protected Set<TriggerEvent> listeners = new HashSet<>();
	protected String custom;

	protected ConfigRule(String name) {
		this.name = name;
		loadFromDefaults();
	}

	public ConfigRule(String name, Map<String, String> parameters) throws HotBlocksException {
		this.name = name;
		loadFromMap(parameters);
		validateConfig();
	}

	public ConfigRule(ConfigurationSection fileSection) throws HotBlocksException {
		this.name = fileSection.getName();
		loadFromFile(fileSection);
		validateConfig();
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

	public Set<Material> getFromMaterials() {
		return new HashSet<>(fromMaterials);
	}

	public boolean hasFromMaterial(Material testMaterial) {
		return fromMaterials.contains(testMaterial);
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

	public int getPoints() {
		return points;
	}

	public Set<TriggerEvent> getListeners() {
		return new HashSet<>(listeners);
	}

	public boolean hasListener(TriggerEvent testListener) {
		return listeners.contains(testListener);
	}

	public String getCustom() {
		return custom;
	}

	public boolean isInWorld(String worldName) {
		if (worlds == null) {
			return true;
		}
		Matcher matcher = worlds.matcher(worldName);
		return matcher.matches();
	}

	public boolean isInRegion(Coordinate blockCoordinate) {
		if (region == null) {
			return true;
		}
		return region.contains(blockCoordinate);
	}

	public boolean isInPermission(Player player) {
		if (permission.isEmpty()) {
			return true;
		}
		return player.hasPermission(permission);
	}

	protected boolean isAttribute(String attributeName) {
		for (ConfigParam myConfigParam : Arrays.asList(ConfigParam.values())) {
			if (myConfigParam.getAttributeName().equalsIgnoreCase(attributeName)) {
				return true;
			}
		}
		return false;
	}

	protected void validateMap(Map<String, String> parameters) throws HotBlocksException {
		for (String myAttributeName : parameters.keySet()) {
			if (!isAttribute(myAttributeName)) {
				throw new HotBlocksException(getName(), "Unknown Attribute", myAttributeName);
			}
		}
	}

	protected String loadStringValue(ConfigParam attribute, Map<String, String> parameters) {
		for (Entry<String, String> myEntry : parameters.entrySet()) {
			if (attribute.getAttributeName().equalsIgnoreCase(myEntry.getKey())) {
				String value = myEntry.getValue();
				if (value != null) {
					return value;
				}
			}
		}
		return attribute.getDefaultValue();
	}

	protected int loadIntValue(ConfigParam attribute, Map<String, String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Value is not integer", value);
		}
	}

	protected float loadFloatValue(ConfigParam attribute, Map<String, String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Value is not numeric", value);
		}
	}

	protected boolean loadBoolValue(ConfigParam attribute, Map<String, String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if ((value.equalsIgnoreCase("TRUE")) || (value.equalsIgnoreCase("YES"))) {
			return true;
		}
		if ((value.equalsIgnoreCase("FALSE")) || (value.equalsIgnoreCase("NO"))) {
			return false;
		}
		throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Value is not boolean", value);
	}

	protected Material loadMaterialValue(ConfigParam attribute, Map<String, String> parameters)
			throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		try {
			return Material.getMaterial(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Unknown Material", value);
		}
	}

	protected Set<Material> loadMaterialsValue(ConfigParam attribute, Map<String, String> parameters)
			throws HotBlocksException {
		Set<Material> materialSet = new HashSet<>();
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return materialSet;
		}
		List<String> materialNames = Arrays.asList(value.split(" "));
		for (String myMaterial : materialNames) {
			try {
				materialSet.add(Material.getMaterial(myMaterial));
			} catch (Exception e) {
				throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Unknown Material",
						myMaterial);
			}
		}
		return materialSet;
	}

	protected Sound loadSoundValue(ConfigParam attribute, Map<String, String> parameters) throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		try {
			return Sound.valueOf(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Unknown Sound", value);
		}
	}

	protected Coordinate loadCoordinateValue(ConfigParam attribute, Map<String, String> parameters)
			throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		String[] coordinates = value.split(";");
		if (coordinates.length != 3) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Not a coordinate format",
					value);
		}
		try {
			int x = Integer.parseInt(coordinates[0]);
			int y = Integer.parseInt(coordinates[1]);
			int z = Integer.parseInt(coordinates[2]);
			return new Coordinate(x, y, z);
		} catch (Exception e) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Not a coordinate format",
					value);
		}
	}

	protected Pattern loadWorldRegexValue(ConfigParam attribute, Map<String, String> parameters)
			throws HotBlocksException {
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return null;
		}
		if (!value.startsWith("^")) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(),
					"Word Regex should start with \"^\"", value);
		}
		try {
			return Pattern.compile(value);
		} catch (Exception e) {
			throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Value is not RegEx", value);
		}
	}

	protected Region buildRegion(Coordinate corner1, Coordinate corner2) throws HotBlocksException {
		if ((corner1 == null) ^ (corner2 == null)) {
			throw new HotBlocksException(getName(), "Second coordinate is missing", null);
		}
		if (corner1 == null) {
			return null;
		}
		return Region.of(corner1, corner2);
	}

	protected Set<TriggerEvent> loadListenersValue(ConfigParam attribute, Map<String, String> parameters)
			throws HotBlocksException {
		Set<TriggerEvent> listenerSet = new HashSet<>();
		String value = loadStringValue(attribute, parameters);
		if (value.isEmpty()) {
			return listenerSet;
		}
		List<String> listenerNames = Arrays.asList(value.split(" "));
		for (String myListener : listenerNames) {
			try {
				myListener = myListener.toUpperCase();
				listenerSet.add(TriggerEvent.valueOf(myListener));
			} catch (Exception e) {
				throw new HotBlocksException(getName() + "." + attribute.getAttributeName(), "Unknown Listener",
						myListener);
			}
		}
		return listenerSet;
	}

	protected void loadFromMap(Map<String, String> parameters) throws HotBlocksException {
		validateMap(parameters);

		Coordinate corner1 = loadCoordinateValue(ConfigParam.CORNER1, parameters);
		Coordinate corner2 = loadCoordinateValue(ConfigParam.CORNER2, parameters);
		this.region = buildRegion(corner1, corner2);

		this.fromMaterials.addAll(loadMaterialsValue(ConfigParam.FROM_MATERIAL, parameters));
		this.toMaterial = loadMaterialValue(ConfigParam.TO_MATERIAL, parameters);
		this.lifetime = loadIntValue(ConfigParam.LIFETIME, parameters);
		this.worlds = loadWorldRegexValue(ConfigParam.WORLDS, parameters);
		this.permission = loadStringValue(ConfigParam.PERMISSION, parameters);
		this.sound = loadSoundValue(ConfigParam.SOUND, parameters);
		this.volume = loadFloatValue(ConfigParam.VOLUME, parameters);
		this.pitch = loadFloatValue(ConfigParam.PITCH, parameters);
		this.continueModify = loadBoolValue(ConfigParam.CONTINUE_MODIFY, parameters);
		this.points = loadIntValue(ConfigParam.POINTS, parameters);
		this.listeners.addAll(loadListenersValue(ConfigParam.LISTENER, parameters));
		this.custom = loadStringValue(ConfigParam.CUSTOM, parameters);
	}

	protected void loadFromDefaults() {
		Map<String, String> dummyMap = new HashMap<>();
		try {
			loadFromMap(dummyMap);
		} catch (HotBlocksException e) {
			e.printStackTrace();
		}
	}

	protected void loadFromFile(ConfigurationSection fileSection) throws HotBlocksException {
		Map<String, String> parameters = new HashMap<>();
		for (String myAttributeName : fileSection.getKeys(false)) {
			String myAttributeValue = fileSection.getString(myAttributeName);
			parameters.put(myAttributeName, myAttributeValue);
		}
		loadFromMap(parameters);
	}

	protected void validateConfig() throws HotBlocksException {
		if ((fromMaterials.isEmpty()) || (toMaterial == null)) {
			throw new HotBlocksException(getName(), "Materials must be defined", null);
		}

		if (lifetime < 1) {
			throw new HotBlocksException(getName() + "." + ConfigParam.LIFETIME.getAttributeName(), "Illegal Lifetime",
					Integer.toString(lifetime));
		}
	}

}
