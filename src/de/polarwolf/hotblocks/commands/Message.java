package de.polarwolf.hotblocks.commands;

public enum Message {

	OK("OK"),
	ERROR("ERROR"),
	JAVA_EXCEPTOPN("Java Exception Error"),
	HELP("Valid commands are: %s"),
	ACTIVE_WORLD_LIST("Active worlds are: %s"),
	NO_ACTIVE_WORLDS("There are no active worlds"),
	UNKNOWN_ACTION("Unknown action"),
	TOO_MANY_PARAMETERS("Too many parameters"),
	UNKNOWN_WORLD("Unknown world"),
	MISSING_WORLDNAME("Worldname is missing"),
	WORLD_NOT_HOT("World not enabled for HotBlocks"),
	WORLD_ALREADY_ENABLED("World is already enabled for HotBlocks"),
	UNKNOWN_OBJECTIVE("Unknown objective"),
	MISSING_OBJECTIVE("Objective is missing"),
	RULES_LOADED("%d rules loaded"),
	LOAD_ERROR("Error loading configuration");

	private final String messageText;

	private Message(String messageText) {
		this.messageText = messageText;
	}

	@Override
	public String toString() {
		return messageText;
	}

}
