package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class HideAllPlayersAction extends Action {

	public HideAllPlayersAction(TIPlugin plugin) {
		super(plugin, "HideAllPlayers", "twitchinteractions.command.action.hideallplayers");
	}

	@Override
	protected void onPlayerEnter(Player player) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (player.canSee(onlinePlayer)) {
				player.hidePlayer(this.getPlugin(), onlinePlayer);
			}
		}
	}

	@Override
	protected void onPlayerLeave(Player player) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (!player.canSee(onlinePlayer)) {
				player.showPlayer(this.getPlugin(), onlinePlayer);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player joinPlayer = event.getPlayer();
		for (Player player : this.getActivePlayers()) {
			if (player.canSee(joinPlayer)) {
				player.hidePlayer(this.getPlugin(), joinPlayer);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player quitPlayer = event.getPlayer();
		for (Player player : this.getActivePlayers()) {
			if (!player.canSee(quitPlayer)) {
				player.showPlayer(this.getPlugin(), quitPlayer);
			}
		}
	}
}
