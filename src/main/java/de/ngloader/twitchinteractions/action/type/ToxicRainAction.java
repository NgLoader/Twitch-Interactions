package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ToxicRainAction extends Action implements Runnable {

	private static final PotionEffect POISION_POTION = new PotionEffect(PotionEffectType.POISON, 20 * 4, 0);

	private BukkitTask currentTask;

	public ToxicRainAction(TIPlugin plugin) {
		super(plugin, "ToxicRain", "twitchinteractions.command.action.toxicrain");
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
		player.setPlayerWeather(WeatherType.DOWNFALL);
	}

	@Override
	protected void onPlayerLeave(Player player) {
		player.removePotionEffect(PotionEffectType.POISON);
		player.resetPlayerWeather();
	}

	@Override
	public void run() {
		for (Player player : this.getActivePlayers()) {
			Location location = player.getLocation();
			World world = player.getWorld();

			boolean applyPotion = true;
			int x = location.getBlockX();
			int z = location.getBlockZ();
			for (int i = location.getBlockY() + 1; i < world.getMaxHeight(); i++) {
				Block block = world.getBlockAt(x, i, z);
				if (this.isValidBlock(block)) {
					if (player.hasPotionEffect(PotionEffectType.POISON)) {
						player.removePotionEffect(PotionEffectType.POISON);
					}
					
					applyPotion = false;
					break;
				}
			}

			if (applyPotion) {
				player.addPotionEffect(POISION_POTION);
			}
		}
	}

	private boolean isValidBlock(Block block) {
		return !block.isPassable() && !block.isLiquid();
	}
}
