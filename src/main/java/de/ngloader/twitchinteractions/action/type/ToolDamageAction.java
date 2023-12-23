package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ToolDamageAction extends Action {

	public ToolDamageAction(TIPlugin plugin) {
		super(plugin, "ToolDamage", "twitchinteractions.command.action.tooldamage");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.containsPlayer(event.getPlayer())) {
			this.damageTool(event.getPlayer().getInventory().getItemInMainHand());
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (this.containsPlayer(event.getPlayer())) {
			this.damageTool(event.getPlayer().getInventory().getItemInMainHand());
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.FARMLAND && this.containsPlayer(event.getPlayer())) {
			this.damageTool(event.getItemInHand());
		}
	}

	public void damageTool(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof Damageable damageable) {
			int maxDamage = item.getType().getMaxDurability();
			int currentDamage = damageable.getDamage();
			int remainingDamage = maxDamage - currentDamage;
			
			int newDamage = currentDamage + (remainingDamage / 2);
			damageable.setDamage(newDamage);
			item.setItemMeta(damageable);
		}
	}
}
