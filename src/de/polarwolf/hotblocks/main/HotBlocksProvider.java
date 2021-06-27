package de.polarwolf.hotblocks.main;

import de.polarwolf.hotblocks.api.HotBlocksAPI;

public class HotBlocksProvider {
	
	private static HotBlocksAPI hotBlocksAPI;
	
	private HotBlocksProvider() {
	}

    protected static void setAPI (HotBlocksAPI newAPI) {
    	hotBlocksAPI=newAPI;
    }
    
    public static HotBlocksAPI getAPI() {
    	return hotBlocksAPI;
    }
    
}
