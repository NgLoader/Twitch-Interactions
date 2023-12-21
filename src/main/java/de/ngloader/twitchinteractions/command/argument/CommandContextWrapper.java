package de.ngloader.twitchinteractions.command.argument;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;

class CommandContextWrapper extends CommandContext<CommandSourceStack> {

	private final CommandContext<CommandSender> context;

	public CommandContextWrapper(CommandContext<CommandSender> context) {
		super(ArgumentTypes.getSourceStack(context.getSource()), context.getInput(), null, null, null, null, context.getRange(), null, null, context.isForked());
		this.context = context;
	}

	@Override
	public <V> V getArgument(String name, Class<V> clazz) {
		return this.context.getArgument(name, clazz);
	}
}	