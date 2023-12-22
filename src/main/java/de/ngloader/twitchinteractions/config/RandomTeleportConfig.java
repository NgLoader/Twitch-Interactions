package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleDouble;

@SimpleSection
public class RandomTeleportConfig {

	@SimpleKey
	@SimpleDouble(defaultValue = 100, min = 10)
	private Double defaultRadius;

	public double getDefaultRadius() {
		return this.defaultRadius;
	}
}