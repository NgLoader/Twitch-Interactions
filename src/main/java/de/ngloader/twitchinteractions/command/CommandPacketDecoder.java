package de.ngloader.twitchinteractions.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;

public class CommandPacketDecoder extends MessageToMessageDecoder<Packet<?>> {

	private final CommandDispatcher<CommandSender> dispatcher;
	private final List<String> aliases;

	private final Connection connection;
	private final Player player;

	public CommandPacketDecoder(CommandRegistry commandRegistry, Player player, Connection connection) {
		this.dispatcher = commandRegistry.getRootDispatcher();
		this.aliases = commandRegistry.getAliases();
		this.connection = connection;
		this.player = player;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out) throws Exception {
		if (msg == null) {
			out.add(msg);
			return;
		}

		if (msg instanceof ServerboundCommandSuggestionPacket packet) {
			String command = packet.getCommand();
			String[] args = command.split(" ");
			if (!this.isAliases(args)) {
				out.add(packet);
				return;
			}

			StringReader cursor = new StringReader(command);
			if (cursor.canRead() && cursor.peek() == '/') {
				cursor.skip();
			}

			ParseResults<CommandSender> result = this.dispatcher.parse(cursor, this.player);
			this.dispatcher.getCompletionSuggestions(result).whenComplete((suggestions, error) -> {
				if (error != null) {
					error.printStackTrace();
					return;
				}
				if (this.player.isOnline() && !suggestions.isEmpty()) {
					this.connection.send(new ClientboundCommandSuggestionsPacket(packet.getId(), suggestions));
				}
			});
		} else {
			out.add(msg);
		}
	}

	private boolean isAliases(String[] args) {
		String command = args[0].toLowerCase();

		if (command.startsWith("/")) {
			command = command.substring(1);
		}

		for (String alias : this.aliases) {
			if (alias.equals(command)) {
				return true;
			}
		}
		return false;
	}
}