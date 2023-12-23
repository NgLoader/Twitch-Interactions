package de.ngloader.twitchinteractions.action.type;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.config.RandomParticleWalkConfig;

public class RandomParticleWalkAction extends Action {

	private final RandomParticleWalkConfig config;

	private final Random random = new Random();
	private final List<Particle> particleList = List.of(
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

	public RandomParticleWalkAction(TIPlugin plugin) {
		super(plugin, "RandomParticleWalk", "twitchinteractions.command.action.randomparticlewalk");
		this.config = plugin.getTIConfig().getRandomParticleWalkConfig();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (this.containsPlayer(player)) {
			Particle particle = this.particleList.get(this.random.nextInt(this.particleList.size()));
			Location location = player.getLocation();
			location.getWorld().spawnParticle(
					particle,
					location.add(0, .2, 0),
					this.config.getParticleAmount(),
					this.config.getParticleRandomPositionX(),
					this.config.getParticleRandomPositionY(),
					this.config.getParticleRandomPositionZ(),
					this.config.getParticleSpeed());
		}
	}
}
