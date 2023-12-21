package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleComment;
import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

@SimpleSection
public class SlipperyHandsConfig {

	@SimpleKey
	@SimpleInteger(min = 1)
	@SimpleComment("Minimum wait delay between drops")
	@SimpleComment("Value time is in seconds")
	private Integer dropDelayMin;

	@SimpleKey
	@SimpleInteger(min = 1)
	@SimpleComment("Random Count added on top of the dropDelayMin value")
	@SimpleComment("Value time is in seconds")
	private Integer dropDelayRandom;

	public Integer getDropDelayMin() {
		return this.dropDelayMin;
	}

	public Integer getDropDelayRandom() {
		return this.dropDelayRandom;
	}
}