package de.ngloader.twitchinteractions.command;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public interface TICommand extends Listener {

	LiteralArgumentBuilder<CommandSender> createArgumentBuilder();

	default void onEnable() {};
	default void onDisable() {};
}
