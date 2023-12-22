package de.ngloader.twitchinteractions;

import org.bukkit.permissions.Permissible;

public enum TIPermission {

	COMMAND_ACTION("twitchinteractions.command.action"),
	COMMAND_DROP_INVENTORY("twitchinteractions.command.dropinventory"),
	COMMAND_FAKE_BAN("twitchinteractions.command.fakeban"),
	COMMAND_FAKE_CRASH("twitchinteractions.command.fakecrash"),
	COMMAND_FAKE_CLOSED("twitchinteractions.command.fakeclosed"),
	COMMAND_SHOW_DEMO("twitchinteractions.command.demo"),
	COMMAND_SHOW_CREDITS("twitchinteractions.command.credits"),
	COMMAND_RANDOM_TELEPORT("twitchinteractions.command.randomteleport"),
	COMMAND_MLG("twitchinteractions.command.mlg");

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
