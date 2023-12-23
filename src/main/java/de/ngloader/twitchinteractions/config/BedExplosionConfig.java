package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

@SimpleSection
public class BedExplosionConfig {

	@SimpleKey
	@SimpleInteger(defaultValue = 3, min = 1)
	private Integer power;

	@SimpleKey
	private Boolean setFire = true;

	@SimpleKey
	private Boolean breakBlocks = false;

	public Integer getPower() {
		return this.power;
	}

	public Boolean isSetFire() {
		return this.setFire;
	}

	public boolean isBreakBlocks() {
		return this.breakBlocks;
	}
}
