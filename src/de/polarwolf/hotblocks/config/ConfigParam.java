package de.polarwolf.hotblocks.config;

/**
 * Parameter which could be used in a Rule
 */
public enum ConfigParam {

	WORLDS("worlds", ""),
	CORNER1("corner1", ""),
	CORNER2("corner2", ""),
	PERMISSION("permission", ""),
	FROM_MATERIAL("fromMaterial", ""),
	TO_MATERIAL("toMaterial", "AIR"),
	LIFETIME("lifetime", "20"),
	SOUND("sound", ""),
	VOLUME("volume", "1"),
	PITCH("Pitch", "0"),
	CONTINUE_MODIFY("continueModify", "FALSE"),
	POINTS("points", "0"),
	LISTENER("listener", "PlayerMoveEvent PlayerTeleportEvent WorldCheck Cascade"),
	CUSTOM("Custom", "");

	private final String attributeName;
	private final String defaultValue;

	private ConfigParam(String attributeName, String defaultValue) {
		this.attributeName = attributeName;
		this.defaultValue = defaultValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
