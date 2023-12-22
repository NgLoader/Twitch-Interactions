package de.ngloader.twitchinteractions.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.ngloader.twitchinteractions.TIPlugin;

public abstract class Action implements Listener {

	private final TIPlugin plugin;

	private final Set<Player> activePlayers = Collections.newSetFromMap(new HashMap<>());
	private final Map<Player, Long> activePlayersExpire = new HashMap<>();

	private boolean enabled = false;

	public Action(TIPlugin plugin) {
		this.plugin = plugin;
	}

	protected void onEnable() {};
	protected void onDisable() {};

	protected void onPlayerEnter(Player player) {};
	protected void onPlayerLeave(Player player) {};

	public void checkExpiredPlayers() {
		long currentTimeMillis = System.currentTimeMillis();
		for (Iterator<Map.Entry<Player, Long>> iterator = this.activePlayersExpire.entrySet().iterator();
				iterator.hasNext();) {
			Map.Entry<Player, Long> entry = iterator.next();
			if (entry.getValue() < currentTimeMillis) {
				iterator.remove();

				this.removePlayer(entry.getKey());
			}
		}
	}

	void enable() {
		if (this.enabled == false) {
			this.enabled = true;

			this.onEnable();
		}
	}

	void disable() {
		if (this.enabled) {
			this.activePlayers.forEach(this::removePlayer);

			this.activePlayers.clear();
			this.activePlayersExpire.clear();

			this.enabled = false;
			this.onDisable();
		}
	}

	public void addPlayer(long expireTime, Player... players) {
		if (!this.isEnabled()) {
			return;
		}

		long currentTimeInMillis = System.currentTimeMillis();
		for (Player player : players) {
			this.activePlayers.add(player);

			if (expireTime > 0) {
				long currentExpire = this.activePlayersExpire.getOrDefault(player, currentTimeInMillis);
				currentExpire += expireTime;
				this.activePlayersExpire.put(player, currentExpire);
			} else {
				this.activePlayersExpire.remove(player);
			}

			this.onPlayerEnter(player);
		}
	}

	public void removePlayer(Player... players) {
		if (!this.isEnabled()) {
			return;
		}

		for (Player player : players) {
			this.activePlayers.remove(player);
			this.activePlayersExpire.remove(player);
			this.onPlayerLeave(player);
		}
	}

	public boolean containsPlayer(Player player) {
		return this.isEnabled() && this.activePlayers.contains(player);
	}

	public Set<Player> getActivePlayers() {
		return Collections.unmodifiableSet(this.activePlayers);
	}

	public TIPlugin getPlugin() {
		return this.plugin;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
}
