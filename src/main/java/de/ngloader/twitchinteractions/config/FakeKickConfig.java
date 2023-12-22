package de.ngloader.twitchinteractions.config;

import de.ngloader.twitchinteractions.config.v3.SimpleKey;
import de.ngloader.twitchinteractions.config.v3.SimpleSection;
import net.md_5.bungee.api.ChatColor;

@SimpleSection
public class FakeKickConfig {

	@SimpleKey
	private String banMessage = "You have been banned from this server!";

	@SimpleKey
	private String serverStopMessage = "§7§o[Server: Server Stopping]";

	@SimpleKey
	private String crashMessage = "Internal exception: java.net.SocketException: Connection reset.";

	public String getBanMessage() {
		return ChatColor.translateAlternateColorCodes('&', this.banMessage);
	}

	public String getServerStopMessage() {
		return ChatColor.translateAlternateColorCodes('&', this.serverStopMessage);
	}

	public String getCrashMessage() {
		return ChatColor.translateAlternateColorCodes('&', this.crashMessage);
	}
}