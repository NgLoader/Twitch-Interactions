package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.TIPermission;
import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.action.ActionManager;
import de.ngloader.twitchinteractions.action.ActionType;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;

public class ActionCommand {

	private final Translation translation;
	private final ActionManager actionManager;

	public ActionCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
		this.actionManager = plugin.getActionManager();
	}

	/*
	 * action <type> add infinity <player>
	 * action <type> add <time> <player>
	 * action <type> remove <player>
	 */

	public LiteralArgumentBuilder<CommandSender> create() {
		return literal("action")
				.requires(TIPermission.COMMAND_TOXIC_RAIN::hasPermission)
				.then(argument("actionType", ArgumentTypes.enumType(ActionType.class))
						.then(literal("enable")
								.executes(this::handleEnable))
						.then(literal("disable")
								.executes(this::handleDisable))
						.then(literal("add")
								.executes(this::handleAddInfinity)
								.then(literal("infinity")
										.executes(this::handleAddInfinity)
										.then(argument("players", ArgumentTypes.players())
												.executes(this::handleAddInfinityPlayers)))
								.then(argument("time", ArgumentTypes.time())
										.executes(this::handleAddTime)
										.then(argument("players", ArgumentTypes.players())
												.executes(this::handleAddTimePlayers))))
						.then(literal("remove")
								.then(argument("players", ArgumentTypes.players())
										.executes(this::handleRemovePlayers))
								.executes(this::handleRemove)));
	}

	public int handleEnable(CommandContext<CommandSender> context) {
		ActionType actionType = ArgumentTypes.getEnum(context, "actionType");
		if (this.actionManager.isRegistered(actionType)) {
			if (this.actionManager.enableAction(actionType)) {
				this.translation.send(context, Message.ACTION_ENABLED, actionType.name());
			} else {
				this.translation.send(context, Message.ACTION_ENABLED_FAILED, actionType.name());
			}
		} else {
			this.translation.send(context.getSource(), Message.ACTION_NOT_REGISTERED, actionType.name());
		}
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleDisable(CommandContext<CommandSender> context) {
		ActionType actionType = ArgumentTypes.getEnum(context, "actionType");
		if (this.actionManager.isRegistered(actionType)) {
			if (this.actionManager.disableAction(actionType)) {
				this.translation.send(context, Message.ACTION_DISABLED, actionType.name());
			} else {
				this.translation.send(context, Message.ACTION_DISABLED_FAILED, actionType.name());
			}
		} else {
			this.translation.send(context.getSource(), Message.ACTION_NOT_REGISTERED, actionType.name());
		}
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddInfinity(CommandContext<CommandSender> context) {
		this.addActionPlayers(context, -1, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddInfinityPlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.addActionPlayers(context, -1, ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddTime(CommandContext<CommandSender> context) {
		long timeOffsetInSeconds = ArgumentTypes.getTime(context, "time") / 20;
		long timeExpire = System.currentTimeMillis() + (timeOffsetInSeconds * 1000);
		this.addActionPlayers(context, timeExpire, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddTimePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		long timeOffsetInSeconds = ArgumentTypes.getTime(context, "time") / 20;
		long timeExpire = System.currentTimeMillis() + (timeOffsetInSeconds * 1000);
		this.addActionPlayers(context, timeExpire, ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleRemove(CommandContext<CommandSender> context) {
		this.removeActionPlayers(context, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleRemovePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.removeActionPlayers(context, ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public void addActionPlayers(CommandContext<CommandSender> context, long time, List<Player> players) {
		ActionType actionType = ArgumentTypes.getEnum(context, "actionType");
		Action action = this.getAction(context, actionType);
		if (action == null) {
			return;
		}

		int count = 0;
		for (Player player : players) {
			if (action.containsPlayer(player)) {
				continue;
			}

			if (time == -1) {
				action.addPlayer(player);
			} else {
				action.addPlayer(time, player);
			}
			count++;
		}

		this.translation.send(context, Message.ACTION_ADDED_PLAYERS, actionType.name(), count);
	}

	public void removeActionPlayers(CommandContext<CommandSender> context, List<Player> players) {
		ActionType actionType = ArgumentTypes.getEnum(context, "actionType");
		Action action = this.getAction(context, actionType);
		if (action == null) {
			return;
		}

		int count = 0;
		for (Player player : players) {
			if (!action.containsPlayer(player)) {
				continue;
			}

			action.removePlayer(player);
			count++;
		}

		this.translation.send(context, Message.ACTION_REMOVED_PLAYERS, actionType.name(), count);
	}

	public Action getAction(CommandContext<CommandSender> context, ActionType actionType) {
		Action action = this.actionManager.getAction(actionType);
		if (action == null) {
			this.translation.send(context.getSource(), Message.ACTION_NOT_REGISTERED, actionType.name());
			return null;
		} else if (!action.isEnabled()) {
			this.translation.send(context.getSource(), Message.ACTION_IS_DISABLED, actionType.name());
			return null;
		}
		return action;
	}
}
