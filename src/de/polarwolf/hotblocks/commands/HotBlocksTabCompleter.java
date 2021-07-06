package de.polarwolf.hotblocks.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class HotBlocksTabCompleter  implements TabCompleter{
	
	protected final HotBlocksCommand hotBlocksCommand;
	
	public HotBlocksTabCompleter(HotBlocksCommand hotBlocksCommand) {
		this.hotBlocksCommand = hotBlocksCommand;
	}


	protected List<String> listCommands() {
		List<String> cmds = new ArrayList<>();
		for (SubCommand subCommand : SubCommand.values()) {
			cmds.add(subCommand.getCommand());
		}
		return cmds;
	}
		
		

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==1) {
			return listCommands();
		}
		
		if (args.length==2) {
			String subCommandName = args[0];
			SubCommand subCommand = hotBlocksCommand.findSubCommand(subCommandName);
			if ((subCommand != null) && (subCommand.isParseWorld())) {
				return hotBlocksCommand.enumWorlds();				
			}
		}

		return new ArrayList<>();
	}

}
