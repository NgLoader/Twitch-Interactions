package de.ngloader.twitchinteractions.command.argument;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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

	public static <T extends Enum<?>> EnumArgumentType<T> enumType(Class<T> enumClass, Function<T, String> mapper) {
		return new EnumArgumentType<>(enumClass, mapper);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T getEnum(CommandContext<?> context, String name) {
		return (T) context.getArgument(name, Enum.class);
	}

	private final Class<T> enumClass;

	private final String[] suggestions;
	private final Map<String, T> mapping = new HashMap<>();

	EnumArgumentType(Class<T> enumClass) {
		this(enumClass, entry -> entry.name());
	}

	EnumArgumentType(Class<T> enumClass, Function<T, String> mapper) {
		this.enumClass = enumClass;

		T[] entries = this.enumClass.getEnumConstants();
		this.suggestions = new String[entries.length];

		for (int i = 0; i < entries.length; i++) {
			T entry = entries[i];
			String mappedName = mapper.apply(entry);
			this.suggestions[i] = mappedName;
			this.mapping.put(mappedName.toLowerCase(), entry);
		}
	}

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		int start = reader.getCursor();
		String input = reader.readString().toLowerCase();

		T entry = this.mapping.get(input);
		if (entry != null) {
			return entry;
		}

		reader.setCursor(start);
		throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(this.suggestions, builder);
	}
}