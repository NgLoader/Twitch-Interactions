package de.ngloader.twitchinteractions.action.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.config.RingOfFireConfig;

public class RingOfFireAction extends Action {

	private final RingOfFireConfig config;

	private final Map<Player, CircleSetting> players = new HashMap<>();

	private BukkitTask currentTask;

	public RingOfFireAction(TIPlugin plugin) {
		super(plugin, "RingOfFire", "twitchinteractions.command.action.ringoffire");
		this.config = plugin.getTIConfig().getRingOfFireConfig();
	}

	@Override
	public void onEnable() {
		if (this.currentTask == null) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this::onUpdate, 20, 10);
		}
	}

	@Override
	public void onDisable() {
		this.players.clear();
		if (this.currentTask != null && !this.currentTask.isCancelled()) {
			this.currentTask.cancel();
		}
	}

	@Override
	protected void onPlayerEnter(Player player) {
		Location location = player.getLocation();
		this.players.put(player, new CircleSetting(location.getWorld(), location.getX(), location.getZ(), this.config.getRadius()));
	}

	@Override
	protected void onPlayerLeave(Player player) {
		this.players.remove(player);
		player.setFireTicks(0);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (this.containsPlayer(event.getEntity())) {
			this.removePlayer(event.getEntity());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CircleSetting circle = this.players.get(player);
		if (circle != null) {
			this.validateLocation(player, circle);
		}
	}

	public void onUpdate() {
		for (Entry<Player, CircleSetting> entry : this.players.entrySet()) {
			Player player = entry.getKey();
			CircleSetting circle = entry.getValue();
			this.validateLocation(player, circle);

			World world = circle.world();
			Location location = player.getLocation().add(0, 1, 0);
			for (double angle = 0; angle <= Math.PI * 2; angle += 0.1) {
				double x = circle.x() + circle.radius() * Math.cos(angle);
				double z = circle.z() + circle.radius() * Math.sin(angle);

				location.setX(x);
				location.setZ(z);

				world.spawnParticle(Particle.SOUL_FIRE_FLAME, location, 2, 0.08, 0.1, 0.08, 0);
			}
		}
	}

	public void validateLocation(Player player, CircleSetting circle) {
		Location location = player.getLocation();
		if (location.getWorld() != circle.world()) {
			this.teleportBack(player, circle);
			return;
		}

		double distance = circle.distance(location);
		if (distance > circle.radius) {
			if (distance > this.config.getDistanceToTeleport()) {
				this.teleportBack(player, circle);
				return;
			}

			double x = circle.x() - location.getX();
			double z = circle.z() - location.getZ();
			player.setVelocity(new Vector(x, 0, z).multiply(0.05).setY(0.2));
			player.setFireTicks(40);
			player.damage(1, player);
		}
	}

	public void teleportBack(Player player, CircleSetting circle) {
		player.teleport(circle.world().getHighestBlockAt((int) circle.x(), (int) circle.z()).getLocation().add(0, 1.25, 0));
	}

	private final record CircleSetting(World world, double x, double z, double radius) {

		public double distance(Location location) {
			return Math.sqrt(NumberConversions.square(x - location.getX()) + NumberConversions.square(z - location.getZ()));
		}
	}
}
