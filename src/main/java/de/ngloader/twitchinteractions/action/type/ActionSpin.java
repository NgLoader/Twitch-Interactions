package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ActionSpin extends Action implements Runnable {

	private BukkitTask currentTask;

	public ActionSpin(TIPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this, 5, 0);
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
	public void run() {
		for (Player player : this.getActivePlayers()) {
			Location location = player.getLocation();
			location.setYaw(location.getYaw() + 1);
			player.teleport(location);
		}
	}
}
