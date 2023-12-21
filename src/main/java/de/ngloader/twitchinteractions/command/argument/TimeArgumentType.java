package de.ngloader.twitchinteractions.command.argument;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.ngloader.twitchinteractions.command.SuggestionProvider;
import de.ngloader.twitchinteractions.util.TimeConverter;

public class TimeArgumentType implements ArgumentType<Long> {

	private static final Pattern PATTERN_INTEGER = Pattern.compile("(?<=\\s|^)\\d+(?=\\s|$)");

	private static final String[] TIME_SUFFIX_LIST = Stream.of(TimeConverter.class.getEnumConstants())
			.map(time -> time.name())
			.toArray(String[]::new);

	private static boolean matchInteger(String input) {
		return PATTERN_INTEGER.matcher(input).find();
	}

	public static TimeArgumentType time() {
		return new TimeArgumentType();
	}

	public static Long getTime(CommandContext<?> context, String name) {
		return (Long) context.getArgument(name, Long.class);
	}

	@Override
	public Long parse(StringReader reader) throws CommandSyntaxException {
		String input = reader.readString();
		long ticks = 0;

		String time = "";
		String type = "";

		for (int i = 0; i < input.length(); i++) {
			String character = String.valueOf(input.charAt(i));

			if (matchInteger(character)) {
				if (type.length() == 0) {
					time += character;
					continue;
				}
			} else if (time.isBlank() && type.isBlank()) {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
			} else {
				type += character;
				continue;
			}

			TimeConverter resultType = TimeConverter.suggestFirst(type);
			if (resultType == null) {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
			}

			ticks += resultType.getTicks() * Integer.valueOf(time);

			time = character;
			type = "";
		}

		if (time.length() != 0) {
			if (type.length() != 0) {
				TimeConverter resultType = TimeConverter.suggestFirst(type);
				if (resultType == null) {
					throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
				}

				ticks += resultType.getTicks() * Integer.valueOf(time);
			} else {
				ticks += TimeConverter.SECOND.getTicks() * Integer.valueOf(time);
			}
		}

		return ticks;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String input = builder.getRemaining();
		String lastLetter = input.isBlank() ? "" : input.substring(input.length() - 1);

		if (matchInteger(lastLetter)) {
			return SuggestionProvider.compareSuggest(builder, "", input, TIME_SUFFIX_LIST);
		}

		if (!lastLetter.isBlank()) {
			StringBuilder lastWordBuilder = new StringBuilder();
			for (int i = input.length() - 1; i >= 0; i--) {
				String character = String.valueOf(input.charAt(i));
				if (matchInteger(character)) {
					break;
				}
				lastWordBuilder.append(character);
			}

			String lastWord = lastWordBuilder.reverse().toString();
			String inputWithoutLastWord = input.substring(0, input.length() - lastWord.length());

			List<TimeConverter> suggestions = TimeConverter.suggest(lastWord);
			if (suggestions.size() > 1) {
				return SuggestionProvider.compareSuggest(builder, lastWord, inputWithoutLastWord, suggestions.stream().map(suggestion -> suggestion.name().toLowerCase()));
			} else if (!suggestions.isEmpty()) {
				String suggestionName = suggestions.get(0).name().toLowerCase();

				if (lastWord != suggestionName) {
					builder.suggest(inputWithoutLastWord + suggestionName);
				}
			} else {
				builder.suggest(inputWithoutLastWord, new LiteralMessage(String.format("§cInvalid input at §8\"§c%s§4%s§8\"", inputWithoutLastWord, lastWord)));
				return builder.buildFuture();
			}
		}

		for (int i = 0; i < 9; i++) {
			builder.suggest(input + i);
		}

		return builder.buildFuture();
	}
}
