package de.polarwolf.hotblocks.commands;

public enum SubCommand {

	ENABLE ("enable", true),
	DISABLE ("disable", true),
	CHECK ("check", true),
	CANCEL ("cancel", true),
	LIST ("list", false),
	RELOAD ("reload", false),
	HELP ("help", false);
	
	
	private final String command;
	private final boolean parseWorld; 
	

	private SubCommand(String command, boolean parseWorld) {
		this.command = command;
		this.parseWorld = parseWorld;
	}


	public String getCommand() {
		return command;
	}


	public boolean isParseWorld() {
		return parseWorld;
	}

}
