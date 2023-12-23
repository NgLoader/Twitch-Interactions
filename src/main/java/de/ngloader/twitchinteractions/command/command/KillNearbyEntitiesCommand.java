package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.TIPermission;
import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.TICommand;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.config.KillNearbyEntitiesConfig;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;

public class KillNearbyEntitiesCommand implements TICommand {

	private final Translation translation;

	private final double radius;
	private final double damage;

	public KillNearbyEntitiesCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();

		KillNearbyEntitiesConfig config = plugin.getTIConfig().getKillNearbyEntitiesConfig();
		this.radius = config.getKillRadius();
		this.damage = config.getDamage();
	}

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("killnearbyentities")
				.requires(TIPermission.COMMAND_KILLNEARBYENTITIES::hasPermission)
				.then(argument("radius", ArgumentTypes.doubleArg(1))
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleRadius)))
				.then(literal("default")
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handle)));
	}

	public int handle(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.killEntities(context, this.radius);
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleRadius(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.killEntities(context, ArgumentTypes.getDouble(context, "radius"));
		return ArgumentBuilder.RESULT_OK;
	}

	public void killEntities(CommandContext<CommandSender> context, double radius) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");
		int killed = 0;

		for (Player player : players) {
			List<LivingEntity> entityList = player.getNearbyEntities(radius, radius, radius)
					.stream()
					.filter(entity -> entity instanceof LivingEntity)
					.filter(entity -> !(entity instanceof ArmorStand))
					.filter(entity -> entity.getCustomName() == null)
					.filter(entity -> {
						if (entity instanceof Tameable tameable) {
							return !tameable.isTamed();
						}
						return true;
					})
					.map(entity -> (LivingEntity) entity)
					.toList();
			for (LivingEntity entity : entityList) {
				entity.damage(this.damage, player);
				killed++;
			}
		}

		this.translation.send(context, Message.COMMAND_KILLNEARBYENTITIES, players.size(), killed);
	}
}
