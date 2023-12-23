package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class StarveAction extends Action implements Runnable {

	private static final PotionEffect POISION_POTION = new PotionEffect(PotionEffectType.HUNGER, 20 * 4, 20);

	private BukkitTask currentTask;

	public StarveAction(TIPlugin plugin) {
		super(plugin, "Starve", "twitchinteractions.command.action.starve");
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
		player.addPotionEffect(POISION_POTION);
	}

	@Override
	protected void onPlayerLeave(Player player) {
		player.removePotionEffect(PotionEffectType.HUNGER);
	}

	@Override
	public void run() {
		for (Player player : this.getActivePlayers()) {
			player.addPotionEffect(POISION_POTION);
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

			if (event.getCause() == DamageCause.STARVATION) {
				player.setHealth(2);
				event.setDamage(1);
			}
		}
	}
}
