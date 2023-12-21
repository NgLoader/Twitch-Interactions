package de.ngloader.twitchinteractions.command.argument;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;

public class EnumArgumentType<T extends Enum<?>> implements ArgumentType<T> {

	public static <T extends Enum<?>> EnumArgumentType<T> enumType(Class<T> enumClass) {
		return new EnumArgumentType<>(enumClass);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T getEnum(CommandContext<?> context, String name) {
		return (T) context.getArgument(name, Enum.class);
	}

	private final Class<T> enumClass;

	EnumArgumentType(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		int start = reader.getCursor();
		String input = reader.readString();
		for (T value : this.enumClass.getEnumConstants()) {
			if (value.name().equalsIgnoreCase(input)) {
				return value;
			}
		}

		reader.setCursor(start);
		throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		Stream<String> values = Arrays.stream(this.enumClass.getEnumConstants()).map(value -> value.name());
		return SharedSuggestionProvider.suggest(values, builder);
	}
}