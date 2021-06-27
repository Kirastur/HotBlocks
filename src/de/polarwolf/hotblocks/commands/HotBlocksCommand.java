package de.polarwolf.hotblocks.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.polarwolf.hotblocks.api.HotBlocksAPI;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.main.Main;

public class HotBlocksCommand implements CommandExecutor{
	
	protected final Main main;
	protected final HotBlocksAPI hotBlocksAPI;
	

	public HotBlocksCommand(Main main, HotBlocksAPI hotBlocksAPI) {
		this.main = main;
		this.hotBlocksAPI = hotBlocksAPI;
	}
	

	protected void cmdHelp(CommandSender sender) {
		List<String> cmds = new ArrayList<>();
		for (SubCommand subCommand : SubCommand.values()) {
			cmds.add(subCommand.getCommand());
		}
		String s = String.join(" ", cmds);
		sender.sendMessage(Message.HELP.toString() + s);
	}


	protected void cmdEnable(World world) {
		hotBlocksAPI.addWorld(world);
	}


	protected void cmdDisable(World world) {
		hotBlocksAPI.removeWorld(world);
	}


	protected void cmdCheck(World world) {
		hotBlocksAPI.checkWorld(world);
	}


	protected void cmdCancel(World world) {
		hotBlocksAPI.cancelWorld(world);
	}

	
	protected void cmdList(CommandSender sender) {
		Set<String> myWorlds = new TreeSet<>();
		for (World world : hotBlocksAPI.getActiveWorlds()) {
			myWorlds.add(world.getName());
		}
		String s = String.join(", ", myWorlds);
		if (!s.isEmpty()) {
			s = Message.ACTIVE_WORLD_LIST.toString() + s;
		} else {
			s = Message.NO_ACTIVE_WORLDS.toString();
		}
		sender.sendMessage(s);		
	}
				

	protected void cmdReload(CommandSender sender) throws HotBlocksException {
		hotBlocksAPI.reload();
		int count = hotBlocksAPI.getRules().size();
		sender.sendMessage(Integer.toString(count) + Message.RULES_LOADED.toString());
	}


	protected void dispatchCommand(CommandSender sender, SubCommand subCommand, World world) {
		try {
			switch (subCommand) {
				case ENABLE:	cmdEnable(world);
								break;
				case DISABLE:	cmdDisable(world);
								break;
				case CHECK:		cmdCheck(world);
								break;
				case CANCEL:	cmdCancel(world);
								break;
				case LIST:		cmdList(sender);
								break;
				case RELOAD:	cmdReload(sender);
								break;
				case HELP:		cmdHelp(sender);
								break;
				default: sender.sendMessage(Message.ERROR.toString());
			}
		} catch (HotBlocksException e) {
			main.getLogger().warning(Message.ERROR.toString()+ " " + e.getMessage());
			sender.sendMessage(e.getMessage());
		}		
	}
	
	
	public SubCommand findSubCommand(String subCommandName) {
		for (SubCommand subCommand : SubCommand.values()) {
			if (subCommand.getCommand().equalsIgnoreCase(subCommandName)) {
				return subCommand;
			}
		}
		return null;
	}
	
	
	public World findWorld(String worldName) {
		for (World world : main.getServer().getWorlds()) {
			if (world.getName().equals(worldName)) {
				return world;
			}
		}
		return null;
	}
	
	
	public List<String> enumWorlds() {
		List<String> worldNames = new ArrayList<>();
		for (World world : main.getServer().getWorlds()) {
			worldNames.add(world.getName());
		}
		return worldNames;
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==0) {
			return false;
		}

		String subCommandName=args[0];
		SubCommand subCommand = findSubCommand(subCommandName);
		if (subCommand == null) {
			sender.sendMessage(Message.UNKNOWN_PARAMETER.toString());
			return true;
		}
		
		if (subCommand.isHasWorld() && (args.length < 2)) {
			sender.sendMessage(Message.MISSING_WORLDNAME.toString());
			return true;
		}
		
		if (((subCommand.isHasWorld())  && (args.length > 2)) ||
		    ((!subCommand.isHasWorld()) && (args.length > 1))) {
			sender.sendMessage(Message.TOO_MANY_PARAMETERS.toString());
			return true;
		}
						
		World world = null;
		if (subCommand.isHasWorld()) {
			String worldName = args[1];
			world = findWorld (worldName);
			if (world == null) {
				sender.sendMessage(Message.UNKNOWN_WORLD.toString());
				return true;
			}
		}

		dispatchCommand (sender, subCommand, world);
		
		return true; 
	}

}


