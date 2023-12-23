package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;
import de.ngloader.twitchinteractions.util.CraftBukkitUtil;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;

public class ScreenCreditsCommand implements TICommand {

	private final Translation translation;

	private final ClientboundGameEventPacket gameEventPacket = new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 1);

	public ScreenCreditsCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
	}

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("credits")
				.requires(TIPermission.COMMAND_SHOW_CREDITS::hasPermission)
				.executes(this::handle)
				.then(argument("players", ArgumentTypes.players())
						.executes(this::handlePlayers));
	}

	public int handle(CommandContext<CommandSender> context) throws CommandSyntaxException {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		CraftBukkitUtil.sendPacket(players, gameEventPacket);

		this.translation.send(context, Message.COMMAND_DEMO, players.size());
		return ArgumentBuilder.RESULT_OK;
	}

	public int handlePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");
		CraftBukkitUtil.sendPacket(players, gameEventPacket);

		this.translation.send(context, Message.COMMAND_DEMO, players.size());
		return ArgumentBuilder.RESULT_OK;
	}
}
