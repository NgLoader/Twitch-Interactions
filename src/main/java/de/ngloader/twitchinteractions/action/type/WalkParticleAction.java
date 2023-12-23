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

public class WalkParticleAction extends Action {

	private final RandomParticleWalkConfig config;

	private final Random random = new Random();
	private final List<Particle> particleList;

	public WalkParticleAction(TIPlugin plugin) {
		super(plugin, "WalkParticle", "twitchinteractions.command.action.walkparticle");
		this.config = plugin.getTIConfig().getRandomParticleWalkConfig();
		this.particleList = this.config.getParticleEffects();
	}

	@Override
	protected boolean canInitialize() {
		return !particleList.isEmpty();
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
