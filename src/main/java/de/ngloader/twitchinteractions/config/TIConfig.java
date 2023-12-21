package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleComment;
import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSectionRoot;

@SimpleSectionRoot(name = "config", version = 1)
public class TIConfig {

	@SimpleKey
	@SimpleComment("Enable verbose console output")
	private Boolean verbose = false;

	@SimpleKey
	@SimpleComment("Command settings for drop inventory")
	private DropInventoryConfig dropInventory;

	@SimpleKey
	@SimpleComment("Action setting for slippery hands")
	private SlipperyHandsConfig slipperyHands;

	public boolean isVerbose() {
		return this.verbose;
	}

	public DropInventoryConfig getDropInventoryConfig() {
		return this.dropInventory;
	}

	public SlipperyHandsConfig getSlipperyHandsConfig() {
		return this.slipperyHands;
	}
}