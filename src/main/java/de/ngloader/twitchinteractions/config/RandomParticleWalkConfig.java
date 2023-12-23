package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleDouble;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

@SimpleSection
public class RandomParticleWalkConfig {

	@SimpleKey
	@SimpleInteger(defaultValue = 10, min = 1)
	private Integer particleAmount;

	@SimpleKey
	@SimpleDouble(defaultValue = 2, min = 0)
	private Double particleRandomPositionX;

	@SimpleKey
	@SimpleDouble(defaultValue = 0.5, min = 0)
	private Double particleRandomPositionY;

	@SimpleKey
	@SimpleDouble(defaultValue = 2, min = 0)
	private Double particleRandomPositionZ;

	@SimpleKey
	@SimpleDouble(defaultValue = 0, min = 0)
	private Double particleSpeed;

	public int getParticleAmount() {
		return this.particleAmount;
	}

	public double getParticleRandomPositionX() {
		return this.particleRandomPositionX;
	}

	public double getParticleRandomPositionY() {
		return this.particleRandomPositionY;
	}

	public double getParticleRandomPositionZ() {
		return this.particleRandomPositionZ;
	}

	public double getParticleSpeed() {
		return this.particleSpeed;
	}
}
