package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleDouble;

@SimpleSection
public class KillNearbyEntitiesConfig {

	@SimpleKey
	@SimpleDouble(defaultValue = 10, min = 1)
	private Double killRadius;

	@SimpleKey
	@SimpleDouble(defaultValue = 50, min = 0.5)
	private Double damage;

	public double getKillRadius() {
		return this.killRadius;
	}

	public double getDamage() {
		return this.damage;
	}
}