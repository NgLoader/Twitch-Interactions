package de.ngloader.twitchinteractions.config;

import java.util.List;

import org.bukkit.Particle;

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

	@SimpleKey
	private List<Particle> particleEffects = List.of(
			Particle.HEART,
			Particle.ASH,
			Particle.CLOUD,
			Particle.VILLAGER_HAPPY,
			Particle.VILLAGER_ANGRY,
			Particle.EXPLOSION_NORMAL,
			Particle.CHERRY_LEAVES,
			Particle.CRIT,
			Particle.CRIT_MAGIC,
			Particle.DRAGON_BREATH,
			Particle.SPELL_WITCH,
			Particle.SNOWFLAKE,
			Particle.NOTE,
			Particle.PORTAL,
			Particle.ELECTRIC_SPARK,
			Particle.DRIP_LAVA,
			Particle.DRIP_WATER);

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

	public List<Particle> getParticleEffects() {
		return this.particleEffects;
	}
}
