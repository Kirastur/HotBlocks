package de.polarwolf.hotblocks.commands;

public enum Message {

	OK ("OK"),
	ERROR ("ERROR"),
	HELP ("Valid commands are: "),
	ACTIVE_WORLD_LIST ("Active worlds are: "),
	NO_ACTIVE_WORLDS ("There are no active worlds"),
	RULES_LOADED (" rules loaded"),
	UNKNOWN_PARAMETER ("Unknown parameter"),
	TOO_MANY_PARAMETERS ("Too many parameters"),
	UNKNOWN_WORLD ("Unknown world"),
	MISSING_WORLDNAME ("Worldname is missing"),
	LOAD_ERROR ("Error loading configuration");
	
	
	private final String messageText;
	

	private Message(String messageText) {
		this.messageText = messageText;
	}


	
	@Override
	public String toString() {
		return messageText;
	}

}
