package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleDouble;

@SimpleSection
public class RingOfFireConfig {

	@SimpleKey
	@SimpleDouble(defaultValue = 5, min = 1)
	private Double radius;

	@SimpleKey
	@SimpleDouble(defaultValue = 20, min = 2)
	private Double distanceToTeleport;

	public double getRadius() {
		return this.radius;
	}

	public Double getDistanceToTeleport() {
		return this.distanceToTeleport;
	}
}
