package de.ngloader.twitchinteractions.action.type;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class JumpOnMoveAction extends Action {

	private final Map<Player, Location> previousLocation = new HashMap<>();

	public JumpOnMoveAction(TIPlugin plugin) {
		super(plugin, "JumpOnMove", "twitchinteractions.command.action.jumponmove");
	}

	@Override
	protected void onPlayerLeave(Player player) {
		this.previousLocation.remove(player);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (this.containsPlayer(player)) {
			Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (blockBelow.getType() == Material.AIR) {
				this.previousLocation.put(player, blockBelow.getLocation());
				return;
			}

			Location current = event.getTo();
			Location previous = this.previousLocation.get(player);
			if (previous != null) {
				double currentX = current.getX();
				double currentZ = current.getZ();
				double previousX = previous.getX();
				double previousZ = previous.getZ();
				double distanceX = Math.max(currentX, previousX) - Math.min(currentX, previousX);
				double distanceZ = Math.max(currentZ, previousZ) - Math.min(currentZ, previousZ);

				if (distanceX < 1 && distanceZ < 1) {
					return;
				}
			}
			this.previousLocation.put(player, current);

			Vector velocity = player.getVelocity();
			if (velocity.getY() < 0.2 && velocity.getY() > -0.2) {
				velocity.setY(1);
				player.setVelocity(velocity);
			}
		}
	}
}
