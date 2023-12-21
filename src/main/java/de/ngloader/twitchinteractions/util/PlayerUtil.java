package de.ngloader.twitchinteractions.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Player;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PlayerUtil {

	private static final Class<?> CRAFT_PLAYER_CLASS = ReflectionUtil.getCraftBukkitClass("entity.CraftPlayer");
	private static final Method GET_HANDLE_METHOD = ReflectionUtil.getMethodByType(CRAFT_PLAYER_CLASS, ServerPlayer.class, 0);

	private static final Field PACKET_LISTENER_CONNECTION_FIELD = ReflectionUtil.getFieldByType(ServerCommonPacketListenerImpl.class, Connection.class, 0);

	public static void sendPacket(List<Player> players, Packet<?>... packets) {
		for (Player player : players) {
			PlayerUtil.sendPacket(player, packets);
		}
	}

	public static void sendPacket(Player[] players, Packet<?>... packets) {
		for (Player player : players) {
			PlayerUtil.sendPacket(player, packets);
		}
	}

	public static void sendPacket(Player player, Packet<?>... packets) {
		ServerGamePacketListenerImpl playerConnection = PlayerUtil.getServerPlayer(player).connection;

		for (Packet<?> packet : packets) {
			playerConnection.send(packet);
		}
	}

	public static ServerPlayer getServerPlayer(Player player) {
		try {
			return (ServerPlayer) GET_HANDLE_METHOD.invoke(player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Connection getConnection(Player player) {
		try {
			ServerPlayer serverPlayer = PlayerUtil.getServerPlayer(player);
			ServerGamePacketListenerImpl serverPacketListener = serverPlayer.connection;
			Connection connection = (Connection) PACKET_LISTENER_CONNECTION_FIELD.get(serverPacketListener);
			return connection;
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
}