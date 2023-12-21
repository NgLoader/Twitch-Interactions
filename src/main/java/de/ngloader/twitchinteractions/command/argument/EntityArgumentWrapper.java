package de.ngloader.twitchinteractions.command.argument;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;

public class EntityArgumentWrapper implements ArgumentType<EntitySelector> {

	private final EntityArgument argument;

	public static EntityArgumentWrapper player() {
		return new EntityArgumentWrapper(EntityArgument.player());
	}

	public static EntityArgumentWrapper players() {
		return new EntityArgumentWrapper(EntityArgument.players());
	}

	public static EntityArgumentWrapper entity() {
		return new EntityArgumentWrapper(EntityArgument.entity());
	}

	public static EntityArgumentWrapper entities() {
		return new EntityArgumentWrapper(EntityArgument.entities());
	}

	public EntityArgumentWrapper(EntityArgument argument) {
		this.argument = argument;
	}

	@Override
	public EntitySelector parse(StringReader reader) throws CommandSyntaxException {
		return argument.parse(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader reader = new StringReader(builder.getInput());
		reader.setCursor(builder.getStart());

		EntitySelectorParser parser = new EntitySelectorParser(reader, true);
		try {
			parser.parse();
		} catch (CommandSyntaxException e) {
			// DONT PRINT (BRIGADIR SYNTAX ERRORS...)
		}

		return parser.fillSuggestions(builder, builder2 -> {
			Collection<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
			SharedSuggestionProvider.suggest(names, builder2);
		});
	}

	@Override
	public Collection<String> getExamples() {
		return this.argument.getExamples();
	}
}