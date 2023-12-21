
package de.ngloader.twitchinteractions.command.suggestion;

import java.util.function.Predicate;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import de.ngloader.twitchinteractions.TIPlugin;

public class CommandSuggestion {

	private final Server server;

	public CommandSuggestion(TIPlugin plugin) {
		this.server = plugin.getServer();
	}

	public SuggestionBuilder<Player, Player> players() {
		return new SuggestionBuilder<>(() -> this.server.getOnlinePlayers().stream()
				.map(player -> (Player) player));
	}

	public SuggestionBuilder<Player, Player> players(Predicate<Player> require) {
		return new SuggestionBuilder<>(() -> this.server.getOnlinePlayers().stream()
				.map(player -> (Player) player)
				.filter(require::test));
	}
}
