package de.ngloader.twitchinteractions.command;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.command.ActionCommand;
import de.ngloader.twitchinteractions.command.command.DropInvCommand;
import de.ngloader.twitchinteractions.command.command.FakeKickCommand;
import de.ngloader.twitchinteractions.command.command.MLGCommand;
import de.ngloader.twitchinteractions.command.command.RandomTeleportCommand;
import de.ngloader.twitchinteractions.command.command.ScreenCreditsCommand;
import de.ngloader.twitchinteractions.command.command.ScreenDemoCommand;
import de.ngloader.twitchinteractions.util.PlayerUtil;
import net.minecraft.network.Connection;

public class CommandRegistry implements Listener {

	private static final String CHANNEL_DECODER_NAME = "twitchinteractions-decoder";

	private final CommandDispatcher<CommandSender> rootDispatcher = new CommandDispatcher<>();
	private final CommandDispatcher<CommandSender> childDispatcher = new CommandDispatcher<>();

	private final TIPlugin plugin;

	private final List<String> aliases = new ArrayList<>();

	public CommandRegistry(TIPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.injectPipeline(event.getPlayer());
	}

	public void registerCommands(PluginCommand pluginCommand) {
		this.register(new ActionCommand(this.plugin).create());
		this.register(new DropInvCommand(this.plugin).create());
		this.register(new FakeKickCommand(this.plugin).create());
		this.register(new ScreenDemoCommand(this.plugin).create());
		this.register(new ScreenCreditsCommand(this.plugin).create());
		this.register(new RandomTeleportCommand(this.plugin).create());
		this.register(new MLGCommand(this.plugin).create());

		CommandBukkit bukkitCommand = new CommandBukkit(this.rootDispatcher, this.plugin.getTranslation());
		pluginCommand.setExecutor(bukkitCommand);
		pluginCommand.setTabCompleter(bukkitCommand);

		this.aliases.add(pluginCommand.getName());
		this.aliases.addAll(pluginCommand.getAliases());

		CommandNode<CommandSender> rootNode = this.childDispatcher.getRoot();
		for (String alias : this.aliases) {
			this.rootDispatcher.register(literal(alias)
					.redirect(rootNode)
					.requires(rootNode.getRequirement()));
		}

		Bukkit.getOnlinePlayers().forEach(this::injectPipeline);
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	private void register(LiteralArgumentBuilder<CommandSender> literal) {
		this.childDispatcher.register(literal);
	}

	public void injectPipeline(Player player) {
		Connection connection = PlayerUtil.getConnection(player);
		if (connection != null) {
			connection.channel.pipeline().addAfter("decoder", CHANNEL_DECODER_NAME, new CommandPacketDecoder(this, player, connection));
		}
	}

	public void uninjectPipeline(Player player) {
		Connection connection = PlayerUtil.getConnection(player);
		if (connection != null) {
			connection.channel.pipeline().remove(CHANNEL_DECODER_NAME);
		}
	}

	public CommandDispatcher<CommandSender> getChildDispatcher() {
		return this.childDispatcher;
	}

	public CommandDispatcher<CommandSender> getRootDispatcher() {
		return this.rootDispatcher;
	}

	public List<String> getAliases() {
		return this.aliases;
	}
}