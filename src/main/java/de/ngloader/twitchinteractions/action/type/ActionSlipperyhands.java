package de.ngloader.twitchinteractions.action.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.config.SlipperyHandsConfig;

public class ActionSlipperyhands extends Action implements Runnable {

	private final Map<Player, Long> dropDelay = new WeakHashMap<>();
	private final Map<Integer, ItemStack> dummySlotPosition = new HashMap<>();

	private final Random random = new Random();

	private final SlipperyHandsConfig config;

	private BukkitTask currentTask;

	public ActionSlipperyhands(TIPlugin plugin) {
		super(plugin);

		this.config = plugin.getTIConfig().getSlipperyHandsConfig();
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this, 20, 20);
		}
	}

	@Override
	protected void onDisable() {
		if (this.currentTask != null) {
			this.currentTask.cancel();
			this.currentTask = null;
		}

		this.dummySlotPosition.clear();
	}

	@Override
	protected void onPlayerEnter(Player player) {
		this.setRandomDropDelay(player);
	}

	@Override
	protected void onPlayerLeave(Player player) {
		this.dropDelay.remove(player);
	}

	@Override
	public void run() {
		long currentTimeInMillis = System.currentTimeMillis();
		for (Map.Entry<Player, Long> entry : this.dropDelay.entrySet()) {
			Player player = entry.getKey();
			Long expireTime = entry.getValue();

			if (currentTimeInMillis < expireTime) {
				continue;
			}

			this.setRandomDropDelay(player);

			PlayerInventory inventory = player.getInventory();
			ItemStack[] content = inventory.getContents();
			
			this.dummySlotPosition.clear();
			for (int slot = 0; slot < content.length; slot++) {
				ItemStack item = content[slot];
				if (item == null || item.getType() == Material.AIR) {
					continue;
				}

				this.dummySlotPosition.put(slot, item);
			}

			int slotCount = this.dummySlotPosition.size();
			if (slotCount == 0) {
				continue;
			}

			int slot = this.dummySlotPosition.keySet().toArray(Integer[]::new)[this.random.nextInt(slotCount)];
			ItemStack item = content[slot];
			inventory.setItem(slot, null);

			Location eyeLocation = player.getEyeLocation();
			World world = eyeLocation.getWorld();
			world.dropItem(eyeLocation.subtract(0, 0.25, 0), item, spawnItem -> {
				spawnItem.setPickupDelay(20 * 2);
				spawnItem.setVelocity(eyeLocation.getDirection().normalize().multiply(0.35));
				world.playSound(eyeLocation, Sound.ITEM_BUNDLE_DROP_CONTENTS, 1, 1);
			});
		}
	}

	public void setRandomDropDelay(Player player) {
		long randomTime = this.config.getDropDelayMin() + this.random.nextInt(this.config.getDropDelayRandom());
		this.dropDelay.put(player, System.currentTimeMillis() + randomTime);
	}
}
