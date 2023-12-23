package de.ngloader.twitchinteractions;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import de.ngloader.twitchinteractions.action.ActionManager;
import de.ngloader.twitchinteractions.command.CommandRegistry;
import de.ngloader.twitchinteractions.command.suggestion.CommandSuggestion;
import de.ngloader.twitchinteractions.config.TIConfig;
import de.ngloader.twitchinteractions.config.v3.SimpleConfig;
import de.ngloader.twitchinteractions.translation.Translation;
import de.ngloader.twitchinteractions.util.TILogger;

public class TIPlugin extends JavaPlugin {

	private SimpleConfig<TIConfig> config;

	private Translation translation;

	private ActionManager actionManager;

	private CommandRegistry commandRegistry;
	private CommandSuggestion commandSuggestion;

	@Override
	public void onLoad() {
		this.config = new SimpleConfig<>(this.getDataFolder().toPath(), TIConfig.class);
	}

	@Override
	public void onEnable() {
		try {
			TIConfig config = this.config.deserialize(true);
			if (config.isVerbose()) {
				TILogger.setVerbose(true);
			}
 
			this.translation = new Translation(this);
			this.translation.initialize();

			this.actionManager = new ActionManager(this);
			this.actionManager.initialize();

			this.commandRegistry = new CommandRegistry(this);
			this.commandSuggestion = new CommandSuggestion(this);

			PluginCommand pluginCommand = this.getCommand("twitchinteractions");
			this.commandRegistry.setPluginCommand(pluginCommand);
			this.commandRegistry.initialize();
		} catch (Exception e) {
			TILogger.error("An error occured while enabling plugin", e);
		}
	}

	@Override
	public void onDisable() {
		if (this.actionManager != null) {
			this.actionManager.shutdown();
		}
		if (this.commandRegistry != null) {
			this.commandRegistry.shutdown();
		}

		Bukkit.getServer().getScheduler().cancelTasks(this);
	}

	public TIConfig getTIConfig() {
		return this.config.getOrDeserializeConfig();
	}

	public Translation getTranslation() {
		return this.translation;
	}

	public ActionManager getActionManager() {
		return this.actionManager;
	}

	public CommandRegistry getCommandRegistry() {
		return this.commandRegistry;
	}

	public CommandSuggestion getCommandSuggestion() {
		return this.commandSuggestion;
	}
}
