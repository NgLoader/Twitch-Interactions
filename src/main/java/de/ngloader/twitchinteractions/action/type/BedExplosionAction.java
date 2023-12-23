package de.ngloader.twitchinteractions.action.type;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import de.ngloader.twitchinteractions.config.BedExplosionConfig;

public class BedExplosionAction extends Action {

	private final BedExplosionConfig config;

	public BedExplosionAction(TIPlugin plugin) {
		super(plugin, "BedExplosion", "twitchinteractions.command.action.bedexplosion");
		this.config = plugin.getTIConfig().getBedExplosionConfig();
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (this.containsPlayer(event.getPlayer())) {
			Location location = event.getBed().getLocation().add(.5, .5, .5);
			location.getWorld().createExplosion(location, config.getPower(), config.isSetFire(), config.isBreakBlocks());
		}
	}
}
