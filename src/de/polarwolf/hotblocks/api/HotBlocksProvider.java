package de.polarwolf.hotblocks.api;

public final class HotBlocksProvider {

	private static HotBlocksAPI hotBlocksAPI = null;

	private HotBlocksProvider() {
	}

	public static boolean hasAPI() {
		return ((hotBlocksAPI != null) && !hotBlocksAPI.isDisabled());
	}

	public static HotBlocksAPI getAPI() {
		if (hasAPI()) {
			return hotBlocksAPI;
		} else {
			return null;
		}
	}

	static boolean setAPI(HotBlocksAPI newAPI) {
		if (!hasAPI()) {
			hotBlocksAPI = newAPI;
			return true;
		} else {
			return false;
		}
	}

}
