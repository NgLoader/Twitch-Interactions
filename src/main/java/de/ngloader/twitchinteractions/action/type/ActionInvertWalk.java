package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ActionInvertWalk extends Action {

	public ActionInvertWalk(TIPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void onPlayerEnter(Player player) {
		player.setWalkSpeed(0.5f);
		player.setFlySpeed(1f);
	}

	@Override
	protected void onPlayerLeave(Player player) {
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (this.containsPlayer(player)) {
			Location from = event.getFrom();
			Location to = event.getTo();

			double offsetX = from.getX() - to.getX();
			double offsetZ = from.getZ() - to.getZ();

			from.add(offsetX, 0, offsetZ);
			from.setY(to.getY());
			event.setTo(from);
		}
	}
}
