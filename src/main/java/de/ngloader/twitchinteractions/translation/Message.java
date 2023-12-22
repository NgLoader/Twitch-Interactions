package de.ngloader.twitchinteractions.translation;

public enum Message {

	PREFIX("prefix", "#d000ff#[#ff006a#Twitch Interactions#d000ff#]§g"),
	ACTION_ENABLED("actionEnabled", "Action §e{0} §gwas §aenabled§8."),
	ACTION_ENABLED_FAILED("actionEnabledFailed", "Action §e{0} §gis already enabled§8."),
	ACTION_DISABLED("actionDisabled", "Action §e{0} §gwas §cdisabled§8."),
	ACTION_DISABLED_FAILED("actionDisabledFailed", "Action §e{0} §gis already disabled§8."),
	ACTION_NOT_REGISTERED("actionNotRegistered", "Action §e{0} §gis not registered§8."),
	ACTION_IS_DISABLED("actionIsDisabled", "Action §e{0} §gis currently disabled§8."),
	ACTION_ADDED_PLAYERS("actionAddedPlayers", "Action §e{0} §ghas added §e{1} §gplayers§8."),
	ACTION_REMOVED_PLAYERS("actionRemovedPlayers", "Action §e{0} §ghas removed §e{1} §gplayers§8."),
	ACTION_ALL_REMOVED_PLAYERS("actionAllRemovedPlayers", "Removed §e{0} §gplayers from all actions§8."),
	COMMAND_DROPPED_INVENTORIES("commandDroppedInventories", "Dropped all items from §e{0} §gplayer inventories§8."),
	COMMAND_FAKE_BAN("commandFakeBan", "Kicked §e{0} %gplayers for reaseon §e{1}§8."),
	COMMAND_DEMO("commandDemo", "Send demo screen to §e{0} §gplayers§8."),
	COMMAND_RANDOM_TELEPORT("commandRandomTeleport", "Teleported §e{0} §gplayers§8."),
	COMMAND_MLG("commandMLG", "§e{0} §gplayers trying now a MLG§8.");

	public static Message findByKey(String key) {
		for (Message messageKey : values()) {
			if (messageKey.key.equalsIgnoreCase(key)) {
				return messageKey;
			}
		}
		return null;
	}

	private final String key;
	private final String defaultMessage;

	private Message(String key, String defaultMessage) {
		this.key = key;
		this.defaultMessage = defaultMessage;
	}

	public String getKey() {
		return this.key;
	}

	public String getDefaultMessage() {
		return this.defaultMessage;
	}
}