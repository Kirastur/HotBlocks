package de.polarwolf.hotblocks.commands;

import static de.polarwolf.hotblocks.commands.ParamType.HOTWORLD;
import static de.polarwolf.hotblocks.commands.ParamType.NONE;
import static de.polarwolf.hotblocks.commands.ParamType.OBJECTIVE;
import static de.polarwolf.hotblocks.commands.ParamType.WORLD;

public enum Action {

	ENABLE("enable",   WORLD, NONE),
	DISABLE("disable", HOTWORLD, NONE),
	CHECK("check",     HOTWORLD, NONE),
	CANCEL("cancel",   HOTWORLD, NONE),
	PAUSE("pause",     HOTWORLD, NONE),
	RESUME("resume",   HOTWORLD, NONE),
	PRINT("print",     HOTWORLD, NONE),
	SETOBJECTIVE("setobjective", HOTWORLD, OBJECTIVE),
	LIST("list",     NONE, NONE),
	RELOAD("reload", NONE, NONE),
	HELP("help",     NONE, NONE);

	private final String command;
	private final ParamType param1;
	private final ParamType param2;

	private Action(String command, ParamType param1, ParamType param2) {
		this.command = command;
		this.param1 = param1;
		this.param2 = param2;
	}

	public String getCommand() {
		return command;
	}

	public int getParamCount() {
		if (param2 != NONE) {
			return 2;
		}
		if (param1 != NONE) {
			return 1;
		}
		return 0;
	}

	public ParamType getParam(int position) {
		if (position == 1) {
			return param1;
		}
		if (position == 2) {
			return param2;
		}
		return NONE;
	}

	public int findPosition(ParamType param) {
		if (param == param1) {
			return 1;
		}
		if (param == param2) {
			return 2;
		}
		return 0;
	}

}
