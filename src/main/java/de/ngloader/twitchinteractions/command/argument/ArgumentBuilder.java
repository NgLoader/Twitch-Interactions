package de.ngloader.twitchinteractions.command.argument;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public class ArgumentBuilder {

	public static final int RESULT_OK = 1;

	public static LiteralArgumentBuilder<CommandSender> literal(String name) {
		return LiteralArgumentBuilder.<CommandSender>literal(name);
	}

	public static <T> RequiredArgumentBuilder<CommandSender, T> argument(final String name,
			final ArgumentType<T> type) {
		return RequiredArgumentBuilder.<CommandSender, T>argument(name, type);
	}

	public static String getSafeStringArgument(CommandContext<CommandSender> context, String fieldName, String defaultValue) {
		String input;
		try {
			input = StringArgumentType.getString(context, fieldName);
		} catch (IllegalArgumentException e) {
			// Ignore missing argument exception and return default value
			return defaultValue;
		}
		return input;
	}
}