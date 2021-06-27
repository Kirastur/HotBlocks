package de.polarwolf.hotblocks.config;

public enum ConfigParam {
	
	WORLDS ("worlds", ""),
	CORNER1 ("Corner1", ""),
	CORNER2 ("Corner2", ""),
	PERMISSION ("Permission", ""),
	FROM_MATERIAL ("fromMaterial", ""),
	TO_MATERIAL ("toMaterial", "AIR"),
	LIFETIME ("Lifetime", "20"),
	SOUND ("Sound", ""),
	VOLUME ("Volume", "1"),
	PITCH ("Pitch", "0"),
	CONTINUE_MODIFY ("continueModify", "FALSE");
	
	
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
