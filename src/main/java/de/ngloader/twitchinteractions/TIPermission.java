package de.ngloader.twitchinteractions;

import org.bukkit.permissions.Permissible;

public enum TIPermission {

	COMMAND_DROP_INVENTORY("twitchinteractions.command.dropinventory"),
	COMMAND_SLIPPERY_HANDS("twitchinteractions.command.slipperyhands"),
	COMMAND_TOXIC_RAIN("twitchinteractions.command.toxicrain");

	private final String permission;

	private TIPermission(String permission) {
		this.permission = permission;
	}

	public boolean hasPermission(Permissible permissible) {
		return permissible.hasPermission(this.permission);
	}

	public String getPermission() {
		return this.permission;
	}
}
