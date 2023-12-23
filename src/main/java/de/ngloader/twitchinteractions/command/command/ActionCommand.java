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
import de.ngloader.twitchinteractions.command.TICommand;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.command.suggestion.CommandSuggestion;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;

public class ActionCommand implements TICommand {

	private final Translation translation;
	private final ActionManager actionManager;
	private final CommandSuggestion suggestion;

	public ActionCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
		this.actionManager = plugin.getActionManager();
		this.suggestion = plugin.getCommandSuggestion();
	}

	/*
	 * action <type> add infinity <player>
	 * action <type> add <time> <player>
	 * action <type> remove <player>
	 * action remove <player>
	 */

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("action")
				.requires(TIPermission.COMMAND_ACTION::hasPermission)
				.then(literal("remove")
						.executes(this::handleRemoveAll)
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleRemoveAllPlayers)))
				.then(argument("actionType", ArgumentTypes.string())
						.suggests(this.suggestion.actions()
								.filter((action, context) -> context.getSource().hasPermission(action.getPermission()))
								.map(action -> action.getName())
								.buildSuggest())
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

	public int handleRemoveAll(CommandContext<CommandSender> context) {
		this.removePlayersFromAllAction(context, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleRemoveAllPlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.removePlayersFromAllAction(context, ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleEnable(CommandContext<CommandSender> context) {
		String actionName = ArgumentTypes.getString(context, "actionType");
		Action action = this.actionManager.getAction(actionName);
		if (action != null) {
			if (action.enable()) {
				this.translation.send(context, Message.ACTION_ENABLED, action.getName());
			} else {
				this.translation.send(context, Message.ACTION_ENABLED_FAILED, action.getName());
			}
		} else {
			this.translation.send(context.getSource(), Message.ACTION_NOT_REGISTERED, actionName);
		}
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleDisable(CommandContext<CommandSender> context) {
		String actionName = ArgumentTypes.getString(context, "actionType");
		Action action = this.actionManager.getAction(actionName);
		if (action != null) {
			if (action.disable()) {
				this.translation.send(context, Message.ACTION_DISABLED, action.getName());
			} else {
				this.translation.send(context, Message.ACTION_DISABLED_FAILED, action.getName());
			}
		} else {
			this.translation.send(context.getSource(), Message.ACTION_NOT_REGISTERED, actionName);
		}
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddInfinity(CommandContext<CommandSender> context) {
		this.addActionPlayers(context, 0, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddInfinityPlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.addActionPlayers(context, 0, ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddTime(CommandContext<CommandSender> context) {
		long timeOffsetInSeconds = ArgumentTypes.getTime(context, "time") / 20;
		this.addActionPlayers(context, timeOffsetInSeconds * 1000, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleAddTimePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		long timeOffsetInSeconds = ArgumentTypes.getTime(context, "time") / 20;
		this.addActionPlayers(context, timeOffsetInSeconds * 1000, ArgumentTypes.getPlayers(context, "players"));
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
		Action action = this.getAction(context);
		if (action == null) {
			return;
		}

		int count = 0;
		for (Player player : players) {
			action.addPlayer(time, player);
			count++;
		}

		this.translation.send(context, Message.ACTION_ADDED_PLAYERS, action.getName(), count);
	}

	public void removeActionPlayers(CommandContext<CommandSender> context, List<Player> players) {
		Action action = this.getAction(context);
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

		this.translation.send(context, Message.ACTION_REMOVED_PLAYERS, action.getName(), count);
	}

	public void removePlayersFromAllAction(CommandContext<CommandSender> context, List<Player> players) {
		for (Action action : this.actionManager.getActionList()) {
			players.forEach(action::removePlayer);
		}

		this.translation.send(context, Message.ACTION_ALL_REMOVED_PLAYERS, players.size());
	}

	public Action getAction(CommandContext<CommandSender> context) {
		String actionName = ArgumentTypes.getString(context, "actionType");
		Action action = this.actionManager.getAction(actionName);
		if (action == null) {
			this.translation.send(context.getSource(), Message.ACTION_NOT_REGISTERED, actionName);
			return null;
		} else if (!action.isEnabled()) {
			this.translation.send(context.getSource(), Message.ACTION_IS_DISABLED, action.getName());
			return null;
		}
		return action;
	}
}
