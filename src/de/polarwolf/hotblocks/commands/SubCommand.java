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
	private final boolean hasWorld; 
	

	private SubCommand(String command, boolean hasWorld) {
		this.command = command;
		this.hasWorld = hasWorld;
	}


	public String getCommand() {
		return command;
	}


	public boolean isHasWorld() {
		return hasWorld;
	}

}
