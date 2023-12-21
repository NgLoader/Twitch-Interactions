package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleDouble;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

@SimpleSection
public class DropInventoryConfig {

	@SimpleKey
	@SimpleDouble(defaultValue = 9.5, min = 1)
	private Double defaultDropInvRange;

	@SimpleKey
	@SimpleInteger(defaultValue = 4, min = 1)
	private Integer defaultDropInvPickupDelay;

	public double getDefaultDropInvRange() {
		return this.defaultDropInvRange;
	}

	public int getDefaultDropInvPickupDelay() {
		return this.defaultDropInvPickupDelay;
	}
}