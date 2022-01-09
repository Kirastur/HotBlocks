package de.polarwolf.hotblocks.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.polarwolf.hotblocks.main.Main;

public class HotBlocksTabCompleter implements TabCompleter {

	protected final HotBlocksCommand hotBlocksCommand;

	public HotBlocksTabCompleter(Main main, HotBlocksCommand hotBlocksCommand) {
		this.hotBlocksCommand = hotBlocksCommand;
		main.getCommand(hotBlocksCommand.getCommandName()).setTabCompleter(this);
	}

	protected List<String> listActions() {
		return hotBlocksCommand.enumActions();
	}

	protected List<String> listWorlds() {
		return hotBlocksCommand.enumWorlds();
	}

	protected List<String> listHotWorlds() {
		return hotBlocksCommand.enumHotWorlds();
	}

	protected List<String> listObjectives() {
		return hotBlocksCommand.enumObjectives();
	}

	protected List<String> handleTabComplete(String[] args) {
		if (args.length < 1) {
			return new ArrayList<>();
		}
		if (args.length == 1) {
			return listActions();
		}

		String actionName = args[0];
		Action action = hotBlocksCommand.findAction(actionName);
		if ((action == null) || (args.length-1 > action.getParamCount())) {
			return new ArrayList<>();
		}

		ParamType paramType = action.getParam(args.length-1);
		if (paramType == ParamType.WORLD) {
			return listWorlds();
		}
		if (paramType == ParamType.HOTWORLD) {
			return listHotWorlds();
		}
		if (paramType == ParamType.OBJECTIVE) {
			return listObjectives();
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return handleTabComplete(args);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(Message.JAVA_EXCEPTOPN.toString());
		}
		return new ArrayList<>();
	}

}
