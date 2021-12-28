package de.polarwolf.hotblocks.api;

public final class HotBlocksProvider {

	private static HotBlocksAPI hotBlocksAPI;

	private HotBlocksProvider() {
	}

	public static HotBlocksAPI getAPI() {
		return hotBlocksAPI;
	}

	static boolean clearAPI() {
		if (hotBlocksAPI == null) {
			return true;
		}
		if (!hotBlocksAPI.isDisabled()) {
			return false;
		}
		hotBlocksAPI = null;
		return true;
	}

	static boolean setAPI(HotBlocksAPI newAPI) {
		if (!clearAPI()) {
			return false;
		}
		hotBlocksAPI = newAPI;
		return true;
	}

}
