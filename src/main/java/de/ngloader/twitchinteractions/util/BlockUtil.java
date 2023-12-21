package de.ngloader.twitchinteractions.util;

import java.util.Collection;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.NumberConversions;

public class BlockUtil {

	public static Block getNonPassableBlockY(World world, double x, double y, double z, int maxDepth) {
		int floorX = NumberConversions.floor(x);
		int floorY = NumberConversions.floor(y);
		int floorZ = NumberConversions.floor(z);

		Block block = world.getBlockAt(floorX, floorY, floorZ);
		if (block.isPassable()) {
			return getNonPassableBlockY(world, floorX, floorY, floorZ, false, maxDepth, 0);
		}

		return getNonPassableBlockY(world, floorX, floorY, floorZ, true, maxDepth, 0);
	}

	private static Block getNonPassableBlockY(World world, int x, int y, int z, boolean up, int maxDepth, int currentDepth) {
		if (maxDepth < currentDepth) {
			return null;
		}

		Block block = world.getBlockAt(x, y, z);
		if (block.isPassable() && up) {
			return block;
		} else if (!block.isPassable() && !up) {
			return world.getBlockAt(x, y + 1, z);
		}

		return getNonPassableBlockY(world, x, y + (up ? 1 : -1), z, up, maxDepth, currentDepth + 1);
	}

	public static double getBlockOffsetY(World world, double x, double y, double z) {
		int floorX = NumberConversions.floor(x);
		int floorY = NumberConversions.floor(y);
		int floorZ = NumberConversions.floor(z);

		double offsetX = Math.max(floorX, x) - Math.min(floorX, x);
		double offsetZ = Math.max(floorZ, z) - Math.min(floorZ, z);
		double offsetY = 0;

		Block block = world.getBlockAt(floorX, floorY, floorZ);
		Collection<BoundingBox> shapes = block.getCollisionShape().getBoundingBoxes();
		for (BoundingBox box : shapes) {
			if (box.getMinX() <= offsetX && box.getMaxX() >= offsetX) {
				if (box.getMinZ() <= offsetZ && box.getMaxZ() >= offsetZ) {
					if (offsetY < box.getMaxY()) {
						offsetY = box.getMaxY();
					}
				}
			}
		}

		return offsetY;
	}
}
