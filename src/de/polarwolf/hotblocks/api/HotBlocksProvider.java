package de.polarwolf.hotblocks.api;

/**
 * Entry point for 3rd party plugins to get access to the HotBlocks API.
 */
public final class HotBlocksProvider {

	private static HotBlocksAPI hotBlocksAPI = null;

	private HotBlocksProvider() {
	}

	/**
	 * Check if the API exists. This can be the default API or a custom API set with
	 * setAPI (if passiveMode is set to TRUE in config).
	 *
	 * @return TRUE if the API exists, otherwise FALSE
	 */
	public static boolean hasAPI() {
		return ((hotBlocksAPI != null) && !hotBlocksAPI.isDisabled());
	}

	/**
	 * Get the Hotblocks-API
	 *
	 * @return Hotblocks API-Object
	 */
	public static HotBlocksAPI getAPI() {
		if (hasAPI()) {
			return hotBlocksAPI;
		} else {
			return null;
		}
	}

	/**
	 * Set the API-object if no one else has set it before. The API-object is
	 * derived from the Orchestrator.
	 *
	 * @param newAPI The new API-object
	 * @return TRUE if setting the new API-object was successful, otherwise FALSE
	 */
	static boolean setAPI(HotBlocksAPI newAPI) {
		if (!hasAPI()) {
			hotBlocksAPI = newAPI;
			return true;
		} else {
			return false;
		}
	}

}
