# HotBlocks
Modify or remove blocks after player has stepped on (minecraft plugin)

There are a lot of minigames out in the world, where a block gets modified instantly or after time when a player steps on them. For example a blue concrete block turns red direct after a player steps on, and vanish 4 seconds later. The goal for the players is to stay as long as possible on the remaining blocks until they fall down.

This plugin delivers only the mechanic for this kind of games. The main features of this plugin are:
* Limit the modification e.g. to a material, a region, or to players having a specific permission etc.
* Multiple transitions (e.g. blue concrete => yellow concrete => red concrete => air) with different timers
* Multiple (non-overlapping) rules e.g. for different materials
* Filter for worlds can be set by regex, so it is compatible with plugins for dynamic world creation and destroying
* Dynamic BoundingBox size, so a player cannot hook on a block without triggering it
* Play a sound when a block gets modified
* Full featured API
* Custom event for a Gaming Framework to count modification initiated by each player

If you want to build a minigame on top of this I suggest to look at "BetonQuest" (https://www.spigotmc.org/resources/betonquest.2117) and "Advanced SlimeWorldManager" (https://www.spigotmc.org/resources/advanced-slimeworldmanager.87209)
