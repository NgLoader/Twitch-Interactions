package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
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

public class MLGCommand implements TICommand {

	private final TIPlugin plugin;
	private final Translation translation;

	private final List<Player> activePlayers = new ArrayList<>();
	private final Map<Player, Integer> bucketSlot = new HashMap<>();

	private final NamespacedKey waterBucketIndex = NamespacedKey.fromString("weaterbucketindex");
	private int nextIndex = 0;

	public MLGCommand(TIPlugin plugin) {
		this.plugin = plugin;
		this.translation = plugin.getTranslation();
	}

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("mlg")
				.requires(TIPermission.COMMAND_MLG::hasPermission)
				.executes(this::handle)
				.then(argument("players", ArgumentTypes.players())
						.executes(this::handlePlayers));
	}

	public int handle(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.addPlayers(context, Lists.newArrayList(Bukkit.getOnlinePlayers()));
		return ArgumentBuilder.RESULT_OK;
	}

	public int handlePlayers(CommandContext<CommandSender> context) throws CommandSyntaxException {
		this.addPlayers(context, ArgumentTypes.getPlayers(context, "players"));
		return ArgumentBuilder.RESULT_OK;
	}

	@Override
	public void onDisable() {
		new ArrayList<>(this.activePlayers).forEach(this::removePlayer);
	}

	public void addPlayers(CommandContext<CommandSender> context, List<Player> players) {
		int count = 0;
		for (Player player : players) {
			if (!this.activePlayers.contains(player)) {
				this.activePlayers.add(player);
				this.equipPlayer(player);
				count++;
			}
		}

		this.translation.send(context, Message.COMMAND_MLG, count);
	}

	public void equipPlayer(Player player) {
		player.setVelocity(new Vector(0, 2, 0));

		PlayerInventory inventory = player.getInventory();
		int slot = new Random().nextInt(9);

		ItemStack item = inventory.getContents()[slot];
		if (item != null && item.getType() != Material.AIR) {
			player.getWorld().dropItem(player.getLocation(), item, spawnItem -> {
				spawnItem.setPickupDelay(20 * 4);
			});
		}

		ItemStack waterBucketItem = new ItemStack(Material.WATER_BUCKET);
		ItemMeta waterBucketMeta = waterBucketItem.getItemMeta();
		waterBucketMeta.getPersistentDataContainer().set(this.waterBucketIndex, PersistentDataType.INTEGER, this.nextIndex++);
		waterBucketItem.setItemMeta(waterBucketMeta);

		this.bucketSlot.put(player, slot);
		inventory.setItem(slot, waterBucketItem);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.removePlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (this.activePlayers.contains(player)) {
			if (player.isFlying()) {
				this.removePlayer(player);
			} else if (player.getVelocity().getY() < 1) {
				Location location = player.getLocation().subtract(0, 0.6, 0);
				Block block = location.getBlock();
				if (!block.isPassable() || block.isLiquid()) {
					this.removePlayer(player);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		this.removePlayer(player);
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (this.activePlayers.contains(player) && event.getBucket() == Material.WATER_BUCKET) {
			event.setCancelled(true);

			Location clickLocation = event.getBlockClicked().getLocation();

			Block placeBlock = clickLocation.getBlock().getRelative(event.getBlockFace());
			if (placeBlock.getType() != Material.AIR) {
				this.removePlayer(player);
				return;
			}

			placeBlock.setType(Material.WATER);
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
				if (placeBlock.getType() == Material.WATER) {
					placeBlock.setType(Material.AIR);
				}
			}, 5);

			this.removePlayer(player);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (this.activePlayers.contains(event.getWhoClicked())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (this.activePlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player player && this.activePlayers.contains(player)) {
			event.setCancelled(true);
		}
	}

	public void removePlayer(Player player) {
		Integer slot = this.bucketSlot.remove(player);
		if (slot != null) {
			player.getInventory().setItem(slot, null);
		}
		this.activePlayers.remove(player);
	}
}
