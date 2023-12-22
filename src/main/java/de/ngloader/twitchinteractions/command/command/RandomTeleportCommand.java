package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.TIPermission;
import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.config.RandomTeleportConfig;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;

public class RandomTeleportCommand {

	private final Translation translation;
	private final RandomTeleportConfig config;

	private final Random random = new Random();

	public RandomTeleportCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
		this.config = plugin.getTIConfig().getRandomTeleportConfig();
	}

	public LiteralArgumentBuilder<CommandSender> create() {
		return literal("randomtp")
				.requires(TIPermission.COMMAND_RANDOM_TELEPORT::hasPermission)
				.then(argument("radius", ArgumentTypes.doubleArg(1))
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleRadius)))
				.then(literal("default")
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handlePlayers)));
	}

	public int handlePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.teleport(context, this.config.getDefaultRadius(), ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleRadius(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.teleport(context, ArgumentTypes.getDouble(context, "radius"), ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	public void teleport(CommandContext<CommandSender> context, double radius, List<Player> players) {
		for (Player player : players) {
			Block block = this.getRandomBlock(player.getLocation(), radius, 10);
			if (block == null) {
				continue;
			}

			player.teleport(block.getLocation().add(0, 1.5, 0));
		}

		this.translation.send(context, Message.COMMAND_RANDOM_TELEPORT, players.size());
	}

	public Block getRandomBlock(Location location, double radius, int trys) {
		World world = location.getWorld();
		Block block = null;

		do {
			double randomX = -radius + this.random.nextDouble(radius * 2);
			double randomZ = -radius + this.random.nextDouble(radius * 2);
			double x = location.getX() + randomX;
			double z = location.getZ() + randomZ;
			
			block = world.getHighestBlockAt((int) x, (int) z);

			trys--;
			if (trys < 0) {
				return block;
			}
		} while (block == null || !block.getType().isSolid() || !block.getType().isOccluding());

		return block;
	}
}
