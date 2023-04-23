package de.polarwolf.hotblocks.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A reload of the configuration is initiated when the "/hotblocks reload"
 * command is executed or the API reload function is called. The reload sends a
 * HotBlocksReloadRequestEvent. If an external plugin wants to set the Ruleset,
 * it must acknowledge the request by setting the flag in the event. Then the
 * plugin should call the
 * {@link de.polarwolf.hotblocks.api.HotBlocksAPI#replaceConfig(de.polarwolf.hotblocks.config.ConfigSection, CommandSender)
 * replaceConfig} API-function to set the new
 * {@link de.polarwolf.hotblocks.config.ConfigSection ConfigSection} (ruleset).
 * If no one acknowledges the Event, the Config Manager assumes that it must use
 * the local config (from the HotBlocks config.yml) and reload this config from
 * file.<BR>
 * On Server start, the plugin cannot directly load the config, because perhaps
 * a later loaded plugin wants it's own config to use. So the first reload is
 * retarded one Minecraft Tick to take care that all plugins are loaded before
 * the load-config is requested.<BR>
 * For reload you can optionally give a CommandSender as initiator. After the
 * reload is finished, the initiator gets a short message.
 */
public class HotBlocksReloadRequestEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	protected final CommandSender initiator;
	protected boolean acknowledged = false;

	HotBlocksReloadRequestEvent(CommandSender initiator) {
		this.initiator = initiator;
	}

	/**
	 * The initiator can be used to send him a message when the reload has finished.
	 */
	public CommandSender getInitiator() {
		return initiator;
	}

	/**
	 * Check if a previous event handler has already acknowledged the reload.
	 * HotBlocks can manage only one Ruleset, so you must take care not to overwrite
	 * the other plugin's config.
	 */
	public boolean isAcknowledged() {
		return acknowledged;
	}

	/**
	 * Inform HotBlocks that you will deliver the Ruleset. If you acknowledge, you
	 * MUST call the replaceConfig API-function (except you have a loading or
	 * parsing error). You do not need to do this inside the event, you are allowed
	 * to do it some ticks later (e.g. use an async task to download the config from
	 * a database).
	 */
	public void acknowledge() {
		acknowledged = true;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
