package de.ngloader.twitchinteractions.action.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.util.CraftBukkitUtil;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("rawtypes")
public class WorldLoadingAction extends Action {

	private static final BiFunction<ResourceKey, String, ResourceKey> RESOURCE_KEY_FACTORY = createResourceKeyFactory();

	private static BiFunction<ResourceKey, String, ResourceKey> createResourceKeyFactory() {
		try {
			Constructor constructor = ResourceKey.class.getDeclaredConstructor(ResourceLocation.class, ResourceLocation.class);
			constructor.setAccessible(true);

			return (resourceKey, worldName) -> {
				ResourceLocation location = new ResourceLocation(resourceKey.location().getNamespace(), worldName);
				try {
					return (ResourceKey) constructor.newInstance(resourceKey.registry(), location);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
					return resourceKey;
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private final Map<Player, CommonPlayerSpawnInfo> previousWorld = new HashMap<>();

	private BukkitTask currentTask;

	public WorldLoadingAction(TIPlugin plugin) {
		super(plugin, "WorldLoading", "twitchinteractions.command.action.worldloading");
	}

	@Override
	protected boolean canInitialize() {
		return RESOURCE_KEY_FACTORY != null;
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this::onUpdate, 20 * 10, 20 * 10);
		}
	}

	@Override
	protected void onDisable() {
		if (this.currentTask != null) {
			this.currentTask.cancel();
			this.currentTask = null;
		}
	}

	@Override
	protected void onPlayerEnter(Player player) {
		this.sendLoadingScreen(player);
	}

	@Override
	protected void onPlayerLeave(Player player) {
		World playerWorld = player.getWorld();
		for (World world : Bukkit.getWorlds()) {
			if (world.equals(playerWorld)) {
				continue;
			}

			Location previousLocation = player.getLocation();
			player.teleport(world.getSpawnLocation());
			player.teleport(previousLocation);
			return;
		}

		player.kickPlayer("Bye, have a great time!");
	}

	public void onUpdate() {
		this.getActivePlayers().forEach(this::sendLoadingScreen);
	}

	@SuppressWarnings("unchecked")
	public void sendLoadingScreen(Player player) {
		ServerPlayer serverPlayer = CraftBukkitUtil.getServerPlayer(player);
		ServerLevel serverLevel = CraftBukkitUtil.getServerLevel(player.getWorld());
		CommonPlayerSpawnInfo spawnInfo = serverPlayer.createCommonSpawnInfo(serverLevel);
		this.previousWorld.putIfAbsent(player, spawnInfo);
		
		ResourceKey resourceKey = RESOURCE_KEY_FACTORY.apply(spawnInfo.dimension(), UUID.randomUUID().toString());
		spawnInfo = new CommonPlayerSpawnInfo(spawnInfo.dimensionType(), (ResourceKey) resourceKey, spawnInfo.seed(),
				spawnInfo.gameType(), spawnInfo.previousGameType(), spawnInfo.isDebug(), spawnInfo.isFlat(),
				spawnInfo.lastDeathLocation(), spawnInfo.portalCooldown());

		ClientboundRespawnPacket respawnPacket = new ClientboundRespawnPacket(spawnInfo, (byte) 3);
		CraftBukkitUtil.sendPacket(player, respawnPacket);
	}
}
