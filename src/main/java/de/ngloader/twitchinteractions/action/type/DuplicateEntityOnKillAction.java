package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.material.Colorable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.config.DuplicateEntityOnKillConfig;

public class DuplicateEntityOnKillAction extends Action {

	private final DuplicateEntityOnKillConfig config;
	private final int despawnTime;

	private final NamespacedKey duplicateEntityKey;

	private BukkitTask currentTask;

	public DuplicateEntityOnKillAction(TIPlugin plugin) {
		super(plugin, "DuplicateEntityOnKill", "twitchinteractions.command.action.duplicateentityonkill");

		this.config = plugin.getTIConfig().getDuplicateEntityOnKill();
		this.despawnTime = this.config.getDespawnTime() * 20;

		this.duplicateEntityKey = NamespacedKey.fromString("duplicatedentity", plugin);
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null && this.config.getDespawnAfterTime()) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this::onUpdate, 1, 20);
		}
	}

	@Override
	protected void onDisable() {
		if (this.currentTask != null) {
			this.currentTask.cancel();
			this.currentTask = null;
		}
	}

	public void onUpdate() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getLivingEntities()) {
				if (entity.getPersistentDataContainer().has(this.duplicateEntityKey, PersistentDataType.BOOLEAN)) {
					if (entity.getTicksLived() > this.despawnTime) {
						double entityHeight = entity.getHeight() / 2;
						world.spawnParticle(Particle.ASH, entity.getLocation().add(0, entityHeight, 0), 25, .4, entityHeight, .4);
						world.playSound(entity.getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1, 2);
						entity.remove();
					}
				}
			}
		}
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
	public void onEntityTame(EntityTameEvent event) {
		if (event.getEntity().getPersistentDataContainer().has(this.duplicateEntityKey, PersistentDataType.BOOLEAN)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getPersistentDataContainer().has(this.duplicateEntityKey, PersistentDataType.BOOLEAN)) {
			event.setCancelled(true);
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

			World world = entity.getWorld();
			if (entity.getPersistentDataContainer().has(this.duplicateEntityKey, PersistentDataType.BOOLEAN)) {
				double entityHeight = entity.getHeight() / 2;
				world.spawnParticle(Particle.ASH, entity.getLocation().add(0, entityHeight, 0), 25, .4, entityHeight, .4);
				world.playSound(entity.getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1, 2);

				entity.remove();
				return;
			}

			entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			entity.playEffect(EntityEffect.TOTEM_RESURRECT);

			if (event.getEntityType() == EntityType.PLAYER) {
				return;
			}

			for (int i = 0; i < this.config.getSummonAmount(); i++) {
				LivingEntity summonEntity = (LivingEntity) world.spawnEntity(entity.getLocation(), entity.getType());
				summonEntity.getPersistentDataContainer().set(this.duplicateEntityKey, PersistentDataType.BOOLEAN, true);
				summonEntity.setCustomName(entity.getCustomName());
				summonEntity.setRemoveWhenFarAway(true);
				summonEntity.setCanPickupItems(false);

				if (summonEntity instanceof Tameable tameable) {
					tameable.setTamed(true);
					tameable.setAgeLock(true);
				}

				if (summonEntity instanceof Colorable colorable) {
					colorable.setColor(((Colorable) entity).getColor());
				}
			}
		}
	}
}
