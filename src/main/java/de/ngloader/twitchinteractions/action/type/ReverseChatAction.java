package de.ngloader.twitchinteractions.action.type;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;

public class ReverseChatAction extends Action {

	public ReverseChatAction(TIPlugin plugin) {
		super(plugin, "ReverseChat", "twitchinteractions.command.action.reversechat");
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if (this.containsPlayer(event.getPlayer())) {
			char[] message = event.getMessage().toCharArray();
			char[] reverse = new char[message.length];

			int length = message.length;
			for (int i = 0; i < length; i++) {
				reverse[length - 1 - i] = message[i];
			}
			
			event.setMessage(new String(reverse));
		}
	}
}
