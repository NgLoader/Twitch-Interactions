package de.ngloader.twitchinteractions.action.type;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class DuplicateEntityOnKillAction extends Action {

	private final NamespacedKey duplicateEntityKey;

	public DuplicateEntityOnKillAction(TIPlugin plugin) {
		super(plugin, "DuplicateEntityOnKill", "twitchinteractions.command.action.duplicateentityonkill");

		this.duplicateEntityKey = NamespacedKey.fromString("duplicatedentity", plugin);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getPersistentDataContainer().has(this.duplicateEntityKey, PersistentDataType.BOOLEAN)) {
			entity.remove();

			event.setDroppedExp(0);
			event.getDrops().clear();
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Player player && this.containsPlayer(player)) {
			this.handleDamage(player, event);
		} else if (damager instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Player player && this.containsPlayer(player)) {
				this.handleDamage(player, event);
			}
		}
	}

	public void handleDamage(Player player, EntityDamageEvent event) {
		if (event.getEntity() instanceof LivingEntity entity) {
			double health = entity.getHealth();
			double finalDamage = event.getFinalDamage();

			if (health - finalDamage >= 0.5) {
				return;
			}
			event.setCancelled(true);

			entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			entity.playEffect(EntityEffect.TOTEM_RESURRECT);

			if (event.getEntityType() == EntityType.PLAYER) {
				return;
			}

			Location location = event.getEntity().getLocation();
			World world = location.getWorld();
			
			LivingEntity summonEntity = (LivingEntity) world.spawnEntity(location, event.getEntityType());
			summonEntity.setRemoveWhenFarAway(true);
			summonEntity.getPersistentDataContainer().set(this.duplicateEntityKey, PersistentDataType.BOOLEAN, true);
		}
	}
}
