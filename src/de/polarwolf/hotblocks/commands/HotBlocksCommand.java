package de.polarwolf.hotblocks.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.polarwolf.hotblocks.api.HotBlocksAPI;
import de.polarwolf.hotblocks.api.HotBlocksProvider;
import de.polarwolf.hotblocks.exception.HotBlocksException;
import de.polarwolf.hotblocks.main.Main;
import de.polarwolf.hotblocks.worlds.HotWorld;
import static de.polarwolf.hotblocks.commands.ParamType.*;

public class HotBlocksCommand implements CommandExecutor {

	protected final Main main;
	protected final String commandName;
	protected HotBlocksTabCompleter tabCompleter;

	public HotBlocksCommand(Main main, String commandName) {
		this.main = main;
		this.commandName = commandName;
		main.getCommand(commandName).setExecutor(this);
		tabCompleter = new HotBlocksTabCompleter(main, this);
	}

	public String getCommandName() {
		return commandName;
	}

	protected HotBlocksAPI getAPI() {
		return HotBlocksProvider.getAPI();
	}

	protected void cmdHelp(CommandSender sender) {
		String s = String.join(" ", enumActions());
		sender.sendMessage(String.format(Message.HELP.toString(), s));
	}

	protected void cmdEnable(World world) throws HotBlocksException {
		if (getAPI().findHotWorld(world) != null) {
			throw new HotBlocksException(Message.WORLD_ALREADY_ENABLED);
		}
		HotWorld hotWorld = getAPI().addHotWorld(world, false);
		if (hotWorld == null) {
			throw new HotBlocksException(Message.ERROR);
		}
	}

	protected void cmdDisable(HotWorld hotWorld) {
		getAPI().removeHotWorld(hotWorld);
	}

	protected void cmdCheck(HotWorld hotWorld) {
		hotWorld.checkWorld();
	}

	protected void cmdCancel(HotWorld hotWorld) {
		getAPI().cancelModifications(hotWorld.getWorld());
	}

	protected void cmdPause(HotWorld hotWorld) {
		hotWorld.setPause(true);
	}

	protected void cmdResume(HotWorld hotWorld) {
		hotWorld.setPause(false);
	}

	protected void cmdPrint(CommandSender sender, HotWorld hotWorld) {
		List<PlayerScore> scores = new ArrayList<>();
		for (UUID myUUID : hotWorld.getScoredPlayers()) {
			scores.add(new PlayerScore(myUUID, hotWorld.getPlayerScore(myUUID)));
		}
		Collections.sort(scores);
		Collections.reverse(scores);
		for (PlayerScore myPlayerScore : scores) {
			int myAmount = myPlayerScore.amount();
			UUID myPlayerUUID = myPlayerScore.playerUUID();
			OfflinePlayer myOfflinePlayer = main.getServer().getOfflinePlayer(myPlayerUUID);
			String myPlayerName = myOfflinePlayer.getName();
			String s = String.format("%20s %4d", myPlayerName, myAmount);
			sender.sendMessage(s);
		}
	}

	protected void cmdSetObjective(HotWorld hotWorld, Objective objective) {
		hotWorld.setObjective(objective);
	}

	protected void cmdList(CommandSender sender) {
		Set<String> worldNames = new TreeSet<>();
		for (HotWorld myHotWorld : getAPI().getHotWorlds()) {
			World myWorld = myHotWorld.getWorld();
			worldNames.add(myWorld.getName());
		}
		String s = String.join(", ", worldNames);
		if (s.isEmpty()) {
			s = Message.NO_ACTIVE_WORLDS.toString();
		} else {
			s = String.format(Message.ACTIVE_WORLD_LIST.toString(), s);
		}
		sender.sendMessage(s);
	}

	protected void cmdReload(CommandSender sender) {
		getAPI().reload(sender);
	}

	protected void dispatchCommand(CommandSender sender, Action action, World world, HotWorld hotWorld,
			Objective objective) {
		try {
			switch (action) {
			case ENABLE:
				cmdEnable(world);
				break;
			case DISABLE:
				cmdDisable(hotWorld);
				break;
			case CHECK:
				cmdCheck(hotWorld);
				break;
			case CANCEL:
				cmdCancel(hotWorld);
				break;
			case PAUSE:
				cmdPause(hotWorld);
				break;
			case RESUME:
				cmdResume(hotWorld);
				break;
			case PRINT:
				cmdPrint(sender, hotWorld);
				break;
			case SETOBJECTIVE:
				cmdSetObjective(hotWorld, objective);
				break;
			case LIST:
				cmdList(sender);
				break;
			case RELOAD:
				cmdReload(sender);
				break;
			case HELP:
				cmdHelp(sender);
				break;
			default:
				sender.sendMessage(Message.ERROR.toString());
			}
		} catch (HotBlocksException hbe) {
			sender.sendMessage(hbe.getMessage());
		} catch (Exception e) {
			sender.sendMessage(Message.JAVA_EXCEPTOPN.toString());
			e.printStackTrace();
		}
	}

	public Action findAction(String actionName) {
		for (Action myAction : Action.values()) {
			if (myAction.getCommand().equalsIgnoreCase(actionName)) {
				return myAction;
			}
		}
		return null;
	}

	public List<String> enumActions() {
		List<String> actionNames = new ArrayList<>();
		for (Action myAction : Action.values()) {
			actionNames.add(myAction.getCommand());
		}
		return actionNames;
	}

	public World findWorld(String worldName) {
		for (World myWorld : main.getServer().getWorlds()) {
			if (myWorld.getName().equals(worldName)) {
				return myWorld;
			}
		}
		return null;
	}

	public List<String> enumWorlds() {
		List<String> worldNames = new ArrayList<>();
		for (World myWorld : main.getServer().getWorlds()) {
			worldNames.add(myWorld.getName());
		}
		return worldNames;
	}

	protected World parseWorld(Action action, String[] args) throws HotBlocksException {
		int worldPosition = action.findPosition(WORLD);
		if (worldPosition == 0) {
			return null;
		}
		if (args.length < worldPosition+1) {
			throw new HotBlocksException(Message.MISSING_WORLDNAME);
		}
		String worldName = args[worldPosition];
		World world = findWorld(worldName);
		if (world == null) {
			throw new HotBlocksException(Message.UNKNOWN_WORLD);
		}
		return world;
	}

	public HotWorld findHotWorld(String hotWorldName) {
		for (HotWorld myHotWorld : getAPI().getHotWorlds()) {
			if (myHotWorld.getWorld().getName().equals(hotWorldName)) {
				return myHotWorld;
			}
		}
		return null;
	}

	public List<String> enumHotWorlds() {
		List<String> hotWorldNames = new ArrayList<>();
		for (HotWorld myHotWorld : getAPI().getHotWorlds()) {
			hotWorldNames.add(myHotWorld.getWorld().getName());
		}
		return hotWorldNames;
	}

	protected HotWorld parseHotWorld(Action action, String[] args) throws HotBlocksException {
		int hotWorldPosition = action.findPosition(HOTWORLD);
		if (hotWorldPosition == 0) {
			return null;
		}
		if (args.length < hotWorldPosition+1) {
			throw new HotBlocksException(Message.MISSING_WORLDNAME);
		}
		String hotWorldName = args[hotWorldPosition];
		World world = findWorld(hotWorldName);
		if (world == null) {
			throw new HotBlocksException(Message.UNKNOWN_WORLD);
		}
		HotWorld hotWorld = getAPI().findHotWorld(world);
		if (hotWorld == null) {
			throw new HotBlocksException(Message.WORLD_NOT_HOT);
		}
		return hotWorld;
	}

	public Objective findObjective(String objectiveName) {
		Scoreboard mainScoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
		for (Objective myObjective : mainScoreboard.getObjectives()) {
			if (myObjective.getName().equals(objectiveName)) {
				return myObjective;
			}
		}
		return null;
	}

	public List<String> enumObjectives() {
		List<String> objectiveNames = new ArrayList<>();
		Scoreboard mainScoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
		for (Objective myObjective : mainScoreboard.getObjectives()) {
			objectiveNames.add(myObjective.getName());
		}
		return objectiveNames;
	}

	protected Objective parseObjective(Action action, String[] args) throws HotBlocksException {
		int objectivePosition = action.findPosition(OBJECTIVE);
		if (objectivePosition == 0) {
			return null;
		}
		if (args.length < objectivePosition+1) {
			throw new HotBlocksException(Message.MISSING_OBJECTIVE);
		}
		String objectiveName = args[objectivePosition];
		Objective objective = findObjective(objectiveName);
		if (objective == null) {
			throw new HotBlocksException(Message.UNKNOWN_OBJECTIVE);
		}
		return objective;
	}

	protected boolean handleCommand(CommandSender sender, String[] args) {
		Action action;
		World world;
		HotWorld hotWorld;
		Objective objective;

		if (args.length == 0) {
			return false;
		}

		try {
			String actionName = args[0];
			action = findAction(actionName);
			if (action == null) {
				throw new HotBlocksException(Message.UNKNOWN_ACTION);
			}
			if (args.length - 1 > action.getParamCount()) {
				throw new HotBlocksException(Message.TOO_MANY_PARAMETERS);
			}
			world = parseWorld(action, args);
			hotWorld = parseHotWorld(action, args);
			objective = parseObjective(action, args);
		} catch (HotBlocksException e) {
			sender.sendMessage(e.getMessage());
			return true;
		}

		dispatchCommand(sender, action, world, hotWorld, objective);
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return handleCommand(sender, args);
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(Message.JAVA_EXCEPTOPN.toString());
		}
		return true;
	}

}
