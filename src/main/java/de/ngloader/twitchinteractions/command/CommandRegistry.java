package de.ngloader.twitchinteractions.command;
import static de.ngloader.twitchinteractions.command.argument.ArgumentBuilder.literal;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.command.command.ActionCommand;
import de.ngloader.twitchinteractions.command.command.DropInvCommand;
import de.ngloader.twitchinteractions.command.command.KickCommand;
import de.ngloader.twitchinteractions.command.command.MLGCommand;
import de.ngloader.twitchinteractions.command.command.RandomTeleportCommand;
import de.ngloader.twitchinteractions.command.command.ScreenCreditsCommand;
import de.ngloader.twitchinteractions.command.command.ScreenDemoCommand;
import de.ngloader.twitchinteractions.util.CraftBukkitUtil;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;

public class CommandRegistry implements Listener {

	private static final String CHANNEL_DECODER_NAME = "twitchinteractions-decoder";

	private final CommandDispatcher<CommandSender> rootDispatcher = new CommandDispatcher<>();
	private final CommandDispatcher<CommandSender> childDispatcher = new CommandDispatcher<>();

	private final List<String> aliases = new ArrayList<>();

	private final List<TICommand> commands = new ArrayList<>();

	private final TIPlugin plugin;

	public CommandRegistry(TIPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.injectPipeline(event.getPlayer());
	}

	public void initialize() {
		this.register(new ActionCommand(this.plugin));
		this.register(new DropInvCommand(this.plugin));
		this.register(new KickCommand(this.plugin));
		this.register(new ScreenDemoCommand(this.plugin));
		this.register(new ScreenCreditsCommand(this.plugin));
		this.register(new RandomTeleportCommand(this.plugin));
		this.register(new MLGCommand(this.plugin));
	}

	public void setPluginCommand(PluginCommand pluginCommand) {
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

	private void register(TICommand command) {
		if (this.commands.contains(command)) {
			throw new IllegalArgumentException("Command " + command.getClass().getSimpleName() + " is already registered!");
		}
		this.commands.add(command);

		LiteralArgumentBuilder<CommandSender> literal = command.createArgumentBuilder();
		command.onEnable();

		this.childDispatcher.register(literal);
		Bukkit.getServer().getPluginManager().registerEvents(command, this.plugin);
	}

	private void injectPipeline(Player player) {
		Connection connection = CraftBukkitUtil.getConnection(player);
		if (connection != null) {
			ChannelPipeline pipeline = connection.channel.pipeline();
			if (pipeline.get(CHANNEL_DECODER_NAME) != null) {
				pipeline.remove(CHANNEL_DECODER_NAME);
			}
			pipeline.addAfter("decoder", CHANNEL_DECODER_NAME, new CommandPacketDecoder(this, player, connection));
		}
	}

	private void uninjectPipeline(Player player) {
		Connection connection = CraftBukkitUtil.getConnection(player);
		if (connection != null) {
			connection.channel.pipeline().remove(CHANNEL_DECODER_NAME);
		}
	}

	public void shutdown() {
		for (TICommand command : this.commands) {
			try {
				command.onDisable();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				HandlerList.unregisterAll(command);
			}
		}
		HandlerList.unregisterAll(this);

		for (Player player : Bukkit.getOnlinePlayers()) {
			this.uninjectPipeline(player);
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