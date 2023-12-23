package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

@SimpleSection
public class RandomPotionConfig {

	@SimpleKey
	@SimpleInteger(defaultValue = 1, min = 1)
	private Integer durationMin;

	@SimpleKey
	@SimpleInteger(defaultValue = 20, min = 1)
	private Integer durationMax;

	@SimpleKey
	@SimpleInteger(defaultValue = 1, min = 1)
	private Integer amplifierMin;

	@SimpleKey
	@SimpleInteger(defaultValue = 4, min = 1)
	private Integer amplifierMax;

	@SimpleKey
	private Boolean ambient = false;

	@SimpleKey
	private Boolean particles = false;

	@SimpleKey
	private Boolean icon = false;

	public Integer getDurationMin() {
		return this.durationMin;
	}

	public Integer getDurationMax() {
		return this.durationMax;
	}

	public Integer getAmplifierMin() {
		return this.amplifierMin;
	}

	public Integer getAmplifierMax() {
		return this.amplifierMax;
	}

	public Boolean isAmbient() {
		return this.ambient;
	}

	public Boolean isParticles() {
		return this.particles;
	}

	public Boolean isIcon() {
		return this.icon;
	}
}
