package de.ngloader.twitchinteractions.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;

import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;
import de.ngloader.twitchinteractions.util.Chat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CommandBukkit implements CommandExecutor, TabExecutor {

	private final CommandDispatcher<CommandSender> dispatcher;
	private final Translation translation;

	public CommandBukkit(CommandDispatcher<CommandSender> dispatcher, Translation translation) {
		this.dispatcher = dispatcher;
		this.translation = translation;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			this.dispatcher.execute(this.getCommand(label, args).trim(), sender);
		} catch (CommandSyntaxException e) {
			BaseComponent componentException = Chat.format(this.translation.getMessage(Message.PREFIX));
			TextComponent componentExceptionContent = new TextComponent(fromMessage(e.getRawMessage()));
			componentExceptionContent.setColor(ChatColor.RED);
			componentException.addExtra(componentExceptionContent);
			sender.spigot().sendMessage(componentException);

			if (e.getInput() != null && e.getCursor() >= 0) {
				int length = Math.min(e.getInput().length(), e.getCursor());
				
				BaseComponent component = Chat.format(this.translation.getMessage(Message.PREFIX));
				component.setColor(ChatColor.GRAY);
				component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/%s %s", label, String.join(" ", args))));
				if (length > 10) {
					component.addExtra("...");
				}

				component.addExtra(e.getInput().substring(Math.max(0, length - 10), length));
				if (length < e.getInput().length()) {
					TextComponent componentError = new TextComponent(e.getInput().substring(length));
					componentError.setColor(ChatColor.RED);
					componentError.setUnderlined(true);
					component.addExtra(componentError);
				}

				TranslatableComponent componentContext = new TranslatableComponent("command.context.here");
				componentContext.setColor(ChatColor.RED);
				componentContext.setItalic(true);
				component.addExtra(componentContext);

				sender.spigot().sendMessage(component);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseComponent component = Chat.format(this.translation.getMessage(Message.PREFIX));
			TranslatableComponent componentFailed = new TranslatableComponent("command.failed");
			Content componentHover = new Text(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
			componentFailed.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Content[] { componentHover }));
			component.addExtra(componentFailed);
			sender.spigot().sendMessage(component);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		StringReader cursor = new StringReader(this.getCommand(label, args));
		if (cursor.canRead() && cursor.peek() == '/') {
			cursor.skip();
		}

		ParseResults<CommandSender> result = this.dispatcher.parse(cursor, sender);
		CompletableFuture<Suggestions> suggestions = this.dispatcher.getCompletionSuggestions(result);

		try {
			return suggestions.get().getList().stream()
					.map(suggestion -> suggestion.getText())
					.toList();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public String getCommand(String label, String[] args) {
		return label + " " + String.join(" ", args);
	}

	public BaseComponent fromMessage(com.mojang.brigadier.Message message) {
		return (BaseComponent) (message instanceof BaseComponent ? (BaseComponent) message : new TextComponent(message.getString()));
	}
}
