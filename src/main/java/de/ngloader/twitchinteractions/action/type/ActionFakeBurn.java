package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ActionFakeBurn extends Action implements Runnable {

	private BukkitTask currentTask;

	public ActionFakeBurn(TIPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this, 20, 20);
		}
	}

	@Override
	protected void onDisable() {
		if (this.currentTask != null) {
			this.currentTask.cancel();
			this.currentTask = null;
		}
	}

	@Override
	protected void onPlayerEnter(Player player) {
		player.setFireTicks(20 * 2);
	}

	@Override
	protected void onPlayerLeave(Player player) {
		player.setFireTicks(0);
	}

	@Override
	public void run() {
		for (Player player : this.getActivePlayers()) {
			player.setFireTicks(20 * 2);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player && this.containsPlayer(player)) {
			double health = player.getHealth();
			double finalDamage = event.getFinalDamage();

			if (health - finalDamage > 1) {
				return;
			}

			if (event.getCause() == DamageCause.FIRE_TICK) {
				player.setHealth(2);
				event.setDamage(1);
			}
		}
	}
}
