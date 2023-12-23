package de.ngloader.twitchinteractions.command.command;

import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.argument;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.List;
import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.ngloader.twitchinteractions.TIPermission;
import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.TICommand;
import de.ngloader.twitchinteractions.command.argument.ArgumentBuilder;
import de.ngloader.twitchinteractions.command.argument.ArgumentTypes;
import de.ngloader.twitchinteractions.config.RandomPotionConfig;
import de.ngloader.twitchinteractions.translation.Message;
import de.ngloader.twitchinteractions.translation.Translation;

public class RandomPotionCommand implements TICommand {

	private final Translation translation;
	private final RandomPotionConfig config;

	private final List<PotionEffectType> effectTypes = List.of(PotionEffectType.values());
	private final Random random = new Random();

	private final int durationMin;
	private final int durationMax;
	private final int amplifierMin;
	private final int amplifierMax;

	public RandomPotionCommand(TIPlugin plugin) {
		this.translation = plugin.getTranslation();
		this.config = plugin.getTIConfig().getRandomPotion();

		this.durationMin = this.config.getDurationMin();
		this.durationMax = this.config.getDurationMax();
		this.amplifierMin = this.config.getAmplifierMin();
		this.amplifierMax = this.config.getAmplifierMax();
	}

	@Override
	public LiteralArgumentBuilder<CommandSender> createArgumentBuilder() {
		return literal("randompotion")
				.requires(TIPermission.COMMAND_RANDOM_POTION::hasPermission)
				.then(argument("players", ArgumentTypes.players())
						.executes(this::handle));
	}

	public int handle(CommandContext<CommandSender> context) throws CommandSyntaxException {
		List<Player> players = ArgumentTypes.getPlayers(context, "players");
		players.forEach(this::applyPotion);
		this.translation.send(context, Message.COMMAND_RANDOM_POTION, players.size());
		return ArgumentBuilder.RESULT_OK;
	}

	public void applyPotion(Player player) {
		if (this.effectTypes.isEmpty()) {
			return;
		}

		PotionEffectType potionEffectType = this.effectTypes.get(this.random.nextInt(this.effectTypes.size()));
		int duration = this.durationMin + this.random.nextInt(this.durationMax - this.durationMin);
		int amplifier = this.amplifierMin + this.random.nextInt(this.amplifierMax - this.amplifierMin);
		PotionEffect potionEffect = new PotionEffect(
				potionEffectType,
				duration * 20, // in ticks
				amplifier,
				this.config.isAmbient(),
				this.config.isParticles(),
				this.config.isIcon());
		player.addPotionEffect(potionEffect);
	}
}
