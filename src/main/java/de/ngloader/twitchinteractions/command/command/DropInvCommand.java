package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.TIPermission;
import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.TICommand;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.config.DropInventoryConfig;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;
import de.ngloader.twitchinteractions.util.BlockUtil;

public class DropInvCommand implements TICommand {

	private final Translation translation;
	private final DropInventoryConfig config;

	private final Random random = new Random();

	public DropInvCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
		this.config = plugin.getTIConfig().getDropInventoryConfig();
	}

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("dropinv")
				.requires(TIPermission.COMMAND_DROP_INVENTORY::hasPermission)
				.then(argument("radius", ArgumentTypes.doubleArg(1, 60))
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handleRadius)))
				.then(literal("default")
						.then(argument("players", ArgumentTypes.players())
								.executes(this::handlePlayers)));
	}

	public int handlePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		double radius = this.config.getDefaultDropInvRange();
		int pickupDelay = this.config.getDefaultDropInvPickupDelay();

		this.dropItems(context, radius, pickupDelay);
		return ArgumentBuilder.RESULT_OK;
	}

	public int handleRadius(CommandContext<CommandSender> context) throws CommandSyntaxException {
		double radius = ArgumentTypes.getDouble(context, "radius");
		int pickupDelay = this.config.getDefaultDropInvPickupDelay();

		this.dropItems(context, radius, pickupDelay);
		return ArgumentBuilder.RESULT_OK;
	}

	public void dropItems(CommandContext<CommandSender> context, double radius, int pickupDelay) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");

		for (Player player : players) {
			ItemStack[] content = player.getInventory().getContents();
			player.getInventory().setContents(new ItemStack[0]);

			for (ItemStack item : content) {
				if (item != null) {
					this.dropItem(player, item, radius, pickupDelay);
				}
			}
		}

		this.translation.send(context, Message.COMMAND_DROPPED_INVENTORIES, players.size());
	}

	public void dropItem(Player player, ItemStack item, double radius, int pickupDelay) {
		Location location = player.getLocation();
		World world = location.getWorld();

		Location spawnLoaction = this.getRandomPosition(location.clone(), radius, 10);
		if (spawnLoaction == null) {
			spawnLoaction = location;
		}

		world.dropItemNaturally(spawnLoaction, item, (itemStack) -> {
			itemStack.setPickupDelay(20 * pickupDelay);
			world.spawnParticle(Particle.CLOUD, itemStack.getLocation().add(0, 0.25, 0), 1, 0, 0, 0, 0);
			world.playSound(itemStack.getLocation(), Sound.AMBIENT_UNDERWATER_EXIT, 0.5f, 1.25f);
		});
	}

	public Location getRandomPosition(Location location, double radius, int trys) {
		World world = location.getWorld();
		Block block;

		do {
			double randomX = -radius + this.random.nextDouble(radius * 2);
			double randomZ = -radius + this.random.nextDouble(radius * 2);
			double x = location.getX() + randomX;
			double z = location.getZ() + randomZ;
			int y = location.getBlockY();

			location.setX(x);
			location.setZ(z);
			block = BlockUtil.getNonPassableBlockY(world, x, y, z, 5);

			trys--;
		} while ((block == null || !block.isPassable()) && trys > 0);

		if (block != null) {
			location.setY(block.getY());
			return location;
		}
		return null;
	}
}
