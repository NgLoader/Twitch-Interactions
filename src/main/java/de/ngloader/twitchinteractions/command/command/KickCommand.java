package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.TIPermission;
import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.TICommand;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.config.FakeKickConfig;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;

public class KickCommand implements TICommand {

	private final Translation translation;
	private final FakeKickConfig config;

	public KickCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
		this.config = plugin.getTIConfig().getFakeKickConfig();
	}

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("kick")
				.then(literal("crash")
						.requires(TIPermission.COMMAND_FAKE_CRASH::hasPermission)
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleCrash)))
				.then(literal("closed")
						.requires(TIPermission.COMMAND_FAKE_CLOSED::hasPermission)
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleClosed)))
				.then(literal("ban")
						.requires(TIPermission.COMMAND_KICK::hasPermission)
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleBan)));
	}

	public int handleCrash(CommandContext<CommandSender> context) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");
		players.forEach(player -> player.kickPlayer(config.getCrashMessage()));

		this.translation.send(context, Message.COMMAND_KICK, players.size(), "crash");
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleClosed(CommandContext<CommandSender> context) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");
		players.forEach(player -> player.kickPlayer(config.getServerStopMessage()));

		this.translation.send(context, Message.COMMAND_KICK, players.size(), "closed");
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleBan(CommandContext<CommandSender> context) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");
		players.forEach(player -> player.kickPlayer(config.getBanMessage()));

		this.translation.send(context, Message.COMMAND_KICK, players.size(), "ban");
		return ArgumentBuilder.RESULT_OK;
	}
}
