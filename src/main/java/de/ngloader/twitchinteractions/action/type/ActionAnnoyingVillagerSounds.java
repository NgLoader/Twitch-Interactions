package de.ngloader.twitchinteractions.action.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ActionAnnoyingVillagerSounds extends Action implements Runnable {

	private final Random random = new Random();

	private BukkitTask currentTask;

	private List<Sound> soundList = new ArrayList<>();

	public ActionAnnoyingVillagerSounds(TIPlugin plugin) {
		super(plugin);

		for (Sound sound : Sound.values()) {
			if (sound.name().contains("VILLAGER") && !sound.name().contains("ZOMBIE_VILLAGER")) {
				this.soundList.add(sound);
			}
		}
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null && this.soundList.size() > 0) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this, 20, 20 * 2);
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
		Sound sound = this.soundList.get(this.random.nextInt(this.soundList.size()));
		float volume = 0.5f + this.random.nextFloat(0.5f);
		float pitch = 0.5f + (this.random.nextFloat() * 1.5f);

		for (Player player : this.getActivePlayers()) {
			Location location = player.getLocation();
			player.playSound(location, sound, SoundCategory.MASTER, volume, pitch);
		}
	}
}
