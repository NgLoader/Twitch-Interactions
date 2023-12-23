package de.ngloader.twitchinteractions.action;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.util.TILogger;

public abstract class Action implements Listener {

	private final TIPlugin plugin;

	private final String name;
	private final String permission;

	private final Set<Player> activePlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Map<Player, Long> activePlayersExpire = new ConcurrentHashMap<>();

	private boolean enabled = false;

	public Action(TIPlugin plugin, String name, String permission) {
		this.plugin = plugin;
		this.name = name;
		this.permission = permission;
	}

	protected void onEnable() {};
	protected void onDisable() {};

	protected void onPlayerEnter(Player player) {};
	protected void onPlayerLeave(Player player) {};

	protected boolean canInitialize() { return true; };

	void checkExpiredPlayers() {
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

	public boolean enable() {
		if (this.enabled) {
			return false;
		}

		this.onEnable();
		this.enabled = true;

		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		pluginManager.registerEvents(this, this.plugin);

		TILogger.info("Enabled action " + this.getName());
		return true;
	}

	public boolean disable() {
		if (!this.enabled) {
			return false;
		}

		this.activePlayers.forEach(this::removePlayer);

		this.activePlayers.clear();
		this.activePlayersExpire.clear();

		this.enabled = false;
		HandlerList.unregisterAll(this);
		this.onDisable();

		TILogger.info("Disabled action " + this.getName());
	return true;
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
			if (this.activePlayers.contains(player)) {
				this.activePlayers.remove(player);
				this.activePlayersExpire.remove(player);
				this.onPlayerLeave(player);
			}
		}
	}

	public boolean containsPlayer(Player player) {
		return this.isEnabled() && player != null && this.activePlayers.contains(player);
	}

	public Set<Player> getActivePlayers() {
		return Collections.unmodifiableSet(this.activePlayers);
	}

	public TIPlugin getPlugin() {
		return this.plugin;
	}

	public String getPermission() {
		return this.permission;
	}

	public String getName() {
		return this.name;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
}
