package de.ngloader.twitchinteractions.action;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.type.AnnoyingVillagerSoundsAction;
import de.ngloader.twitchinteractions.action.type.BedExplosionAction;
import de.ngloader.twitchinteractions.action.type.BurnAction;
import de.ngloader.twitchinteractions.action.type.DuplicateEntityOnKillAction;
import de.ngloader.twitchinteractions.action.type.HideAllPlayersAction;
import de.ngloader.twitchinteractions.action.type.InvertWalkAction;
import de.ngloader.twitchinteractions.action.type.JumpOnMoveAction;
import de.ngloader.twitchinteractions.action.type.LagAction;
import de.ngloader.twitchinteractions.action.type.PreventBedSleepAction;
import de.ngloader.twitchinteractions.action.type.ReverseChatAction;
import de.ngloader.twitchinteractions.action.type.RickRollAction;
import de.ngloader.twitchinteractions.action.type.RingOfFireAction;
import de.ngloader.twitchinteractions.action.type.SlipperyhandsAction;
import de.ngloader.twitchinteractions.action.type.SpinAction;
import de.ngloader.twitchinteractions.action.type.StarveAction;
import de.ngloader.twitchinteractions.action.type.ToolDamageAction;
import de.ngloader.twitchinteractions.action.type.ToxicRainAction;
import de.ngloader.twitchinteractions.action.type.WalkParticleAction;
import de.ngloader.twitchinteractions.action.type.WorldLoadingAction;
import de.ngloader.twitchinteractions.util.TILogger;

public class ActionManager implements Listener, Runnable {

	private final TIPlugin plugin;

	private final Map<Class<? extends Action>, Action> actionByClass = new HashMap<>();
	private final Map<String, Action> actionByName = new HashMap<>();

	private BukkitTask actionTask;

	public ActionManager(TIPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (Action action : this.actionByClass.values()) {
			if (action.isEnabled()) {
				action.checkExpiredPlayers();
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (Action action : this.actionByClass.values()) {
			action.removePlayer(event.getPlayer());
		}
	}

	public void initialize() {
		this.registerAction(AnnoyingVillagerSoundsAction.class, new AnnoyingVillagerSoundsAction(this.plugin));
		this.registerAction(BedExplosionAction.class, new BedExplosionAction(this.plugin));
		this.registerAction(BurnAction.class, new BurnAction(this.plugin));
		this.registerAction(DuplicateEntityOnKillAction.class, new DuplicateEntityOnKillAction(this.plugin));
		this.registerAction(LagAction.class, new LagAction(this.plugin));
		this.registerAction(HideAllPlayersAction.class, new HideAllPlayersAction(this.plugin));
		this.registerAction(InvertWalkAction.class, new InvertWalkAction(this.plugin));
		this.registerAction(JumpOnMoveAction.class, new JumpOnMoveAction(this.plugin));
		this.registerAction(PreventBedSleepAction.class, new PreventBedSleepAction(this.plugin));
		this.registerAction(WalkParticleAction.class, new WalkParticleAction(this.plugin));
		this.registerAction(ReverseChatAction.class, new ReverseChatAction(this.plugin));
		this.registerAction(RickRollAction.class, new RickRollAction(this.plugin));
		this.registerAction(RingOfFireAction.class, new RingOfFireAction(this.plugin));
		this.registerAction(SlipperyhandsAction.class, new SlipperyhandsAction(this.plugin));
		this.registerAction(SpinAction.class, new SpinAction(this.plugin));
		this.registerAction(StarveAction.class, new StarveAction(this.plugin));
		this.registerAction(ToolDamageAction.class, new ToolDamageAction(this.plugin));
		this.registerAction(ToxicRainAction.class, new ToxicRainAction(this.plugin));
		this.registerAction(WorldLoadingAction.class, new WorldLoadingAction(this.plugin));

		for (Action action : this.actionByClass.values()) {
			try {
				action.enable();
			} catch (Exception e) {
				TILogger.error("Error by enabling action " + action.getClass().getSimpleName(), e);
			}
		}

		if (this.actionTask == null) {
			this.actionTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this, 20, 20);
		}
	}

	private <T extends Action> void registerAction(Class<T> actionClass, T action) {
		if (!action.canInitialize()) {
			TILogger.info("Action '" + actionClass.getSimpleName() + "' can't be initialized!");
			return;
		}

		if (this.actionByClass.putIfAbsent(actionClass, action) != null) {
			TILogger.info("Action class '" + actionClass.getSimpleName() + "' is already registered!");
			return;
		}

		String actionName = action.getName().toLowerCase();
		if (this.actionByName.putIfAbsent(actionName, action) != null) {
			this.actionByClass.remove(actionClass);
			TILogger.info("Action name '" + actionName + "' is already registered!");
			return;
		}

		TILogger.info("Action " + action.getName() + " is now registered.");
	}

	public void shutdown() {
		for (Action action : this.actionByClass.values()) {
			try {
				action.disable();
			} catch (Exception e) {
				TILogger.error("Error by disabling action " + action.getClass().getSimpleName(), e);
			}
		}
		this.actionByClass.clear();
		this.actionByName.clear();

		if (this.actionTask != null) {
			this.actionTask.cancel();
			this.actionTask = null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Action> T getAction(String actionName) {
		Action action = this.actionByName.get(actionName.toLowerCase());
		return action != null ? (T) action : null;
	}

	public <T extends Action> T getAction(Class<T> actionClass) {
		Action action = this.actionByClass.get(actionClass);
		return action != null ? actionClass.cast(action) : null;
	}

	public boolean isRegistered(Class<?> actionClass) {
		return this.actionByClass.containsKey(actionClass);
	}

	public boolean isRegistered(String actionName) {
		return this.actionByName.containsKey(actionName.toLowerCase());
	}

	public Collection<Action> getActionList() {
		return Collections.unmodifiableCollection(this.actionByClass.values());
	}
}
