package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleComment;
import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

@SimpleSection
public class DuplicateEntityOnKillConfig {

	@SimpleKey
	private Boolean despawnAfterTime = true;

	@SimpleKey
	@SimpleInteger(defaultValue = 30, min = 1)
	@SimpleComment("Despawn time in seconds")
	private Integer despawnTime;

	@SimpleKey
	@SimpleInteger(defaultValue = 1, min = 1)
	private Integer summonAmount;

	public Boolean getDespawnAfterTime() {
		return this.despawnAfterTime;
	}

	public Integer getDespawnTime() {
		return this.despawnTime;
	}

	public Integer getSummonAmount() {
		return this.summonAmount;
	}
}
