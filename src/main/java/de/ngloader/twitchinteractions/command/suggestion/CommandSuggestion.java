
package de.ngloader.twitchinteractions.command.suggestion;

import java.util.function.Predicate;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.action.ActionManager;

public class CommandSuggestion {

	private final Server server;
	private final ActionManager actionManager;

	public CommandSuggestion(TIPlugin plugin) {
		this.server = plugin.getServer();
		this.actionManager = plugin.getActionManager();
	}

	public SuggestionBuilder<Action, Action, CommandSender> actions() {
		return new SuggestionBuilder<>(() -> this.actionManager.getActionList().stream());
	}

	public SuggestionBuilder<Player, Player, CommandSender> players() {
		return new SuggestionBuilder<>(() -> this.server.getOnlinePlayers().stream()
				.map(player -> (Player) player));
	}

	public SuggestionBuilder<Player, Player, CommandSender> players(Predicate<Player> require) {
		return new SuggestionBuilder<>(() -> this.server.getOnlinePlayers().stream()
				.map(player -> (Player) player)
				.filter(require::test));
	}
}
