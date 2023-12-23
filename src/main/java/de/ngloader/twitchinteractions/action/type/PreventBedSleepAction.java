package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class PreventBedSleepAction extends Action {

	public PreventBedSleepAction(TIPlugin plugin) {
		super(plugin, "PreventBedSleep", "twitchinteractions.command.action.preventbedsleep");
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		if (this.containsPlayer(player)) {
			event.setCancelled(true);

			Location location = player.getLocation();
			Location bed = event.getBed().getLocation().add(.5, .5, .5);
			double xDiff = bed.getX() - location.getX();
			double yDiff = bed.getY() - location.getY() - player.getEyeHeight();
			double zDiff = bed.getZ() - location.getZ();

			double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

			double yaw = Math.toDegrees(Math.atan2(zDiff, xDiff));
			double pitch = Math.toDegrees(Math.atan2(yDiff, distanceXZ));
			location.setYaw((float) yaw);
			location.setPitch((float) pitch);

			player.teleport(location);
			player.playSound(location, Sound.ENTITY_VILLAGER_NO, 1, 1.5f);
		}
	}
}
