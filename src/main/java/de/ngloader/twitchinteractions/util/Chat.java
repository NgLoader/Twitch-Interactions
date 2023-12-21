package de.ngloader.twitchinteractions.util;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Chat {

	public static BaseComponent format(String message, Object... args) {
		char[] array = message.toCharArray();

		TextComponent mainComponent = new TextComponent();
		TextComponent workComponent = new TextComponent();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			char letter = array[i];

			if (array[i] == '#' && (i == 0 || array[i - 1] != '\\')) {
				if (!builder.isEmpty()) {
					workComponent.setText(builder.toString());
					mainComponent.addExtra(workComponent);

					workComponent = new TextComponent();
					builder.setLength(0);
				}

				StringBuilder colorBuilder = new StringBuilder();
				for (int j = 1; j < 8; j++) {
					int colorIndex = i + j;
					
					if (array.length <= colorIndex || array[colorIndex] == ' ' || array[colorIndex] == '#') {
						i++;
						break;
					} else if (j != 7) {
						colorBuilder.append(array[colorIndex]);
					}
				}

				i += colorBuilder.length();
				try {
					ChatColor color = ChatColor.of("#" + colorBuilder.toString());
					workComponent.setColor(color);
				} catch (IllegalArgumentException e) {
				}
				continue;
			}

			if (array[i] == '{' && array.length > i + 2 && array[i + 2] == '}') {	
				try {
					int index = Integer.valueOf(String.valueOf(array[i + 1]));

					i += 2;

					if (args.length > index) {
						builder.append(args[index].toString());
					}
				} catch (NumberFormatException e) {
				}
				continue;
			}

			builder.append(letter);
		}

		if (!builder.isEmpty()) {
			workComponent.setText(builder.toString());
			mainComponent.addExtra(workComponent);
		}

		return mainComponent;
	}

	public static void send(CommandSender sender, String message, Object... args) {
		sender.spigot().sendMessage(Chat.format(message, args));
	}
}
