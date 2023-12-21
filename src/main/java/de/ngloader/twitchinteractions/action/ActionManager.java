package de.ngloader.twitchinteractions.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.util.TILogger;

public class ActionManager implements Listener, Runnable {

	private final TIPlugin plugin;

	private final Map<ActionType, Action> actionTypes = new HashMap<>();

	private BukkitTask actionTask;

	public ActionManager(TIPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (Action action : this.actionTypes.values()) {
			if (action.isEnabled()) {
				action.checkExpiredPlayers();
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (Action action : this.actionTypes.values()) {
			action.removePlayer(event.getPlayer());
		}
	}

	public void initialize() {
		this.registerAction(ActionType.TOXICRAIN);
		this.registerAction(ActionType.SLIPPERYHANDS);
		this.registerAction(ActionType.RICKROLL);

		this.actionTypes.keySet().forEach(this::enableAction);

		if (this.actionTask == null) {
			this.actionTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this, 20, 20);
		}
	}

	private void registerAction(ActionType type) {
		if (this.actionTypes.containsKey(type)) {
			throw new IllegalStateException("Action is already registered!");
		}

		try {
			Action instance = type.newInstance(this.plugin);
			this.actionTypes.put(type, instance);

			TILogger.info("Action registered: " + type.name());
		} catch (Exception e) {
			TILogger.error("A error occured by register action: " + type.name(), e);
		}
	}

	public boolean enableAction(ActionType actionType) {
		Action action = this.actionTypes.get(actionType);
		if (action == null || action.isEnabled()) {
			return false;
		}

		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		pluginManager.registerEvents(action, this.plugin);

		action.enable();

		TILogger.info("Action enabled: " + actionType.name());
		return true;
	}

	public boolean disableAction(ActionType actionType) {
		Action action = this.actionTypes.get(actionType);
		if (action == null || !action.isEnabled()) {
			return false;
		}

		HandlerList.unregisterAll(action);

		action.disable();

		TILogger.info("Action disabled: " + actionType.name());
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T extends Action> T getAction(ActionType type) {
		return (T) this.actionTypes.get(type);
	}

	public void shutdown() {
		this.actionTypes.keySet().forEach(this::disableAction);
		this.actionTypes.clear();

		if (this.actionTask != null) {
			this.actionTask.cancel();
			this.actionTask = null;
		}
	}

	public boolean isRegistered(ActionType actionType) {
		return this.actionTypes.containsKey(actionType);
	}

	public Map<ActionType, Action> getActionTypes() {
		return Collections.unmodifiableMap(this.actionTypes);
	}
}
