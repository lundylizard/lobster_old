package me.lundy.lobster.command;

import me.lundy.lobster.commands.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    // private final SettingsManager settingsManager;

    public CommandManager() {
        // this.settingsManager = new SettingsManager(Lobster.getInstance().getDatabase().getDataSource(), )
        registerCommand(new HelpCommand());
        registerCommand(new JoinCommand());
        registerCommand(new LeaveCommand());
        registerCommand(new LoopCommand());
        registerCommand(new LyricsCommand());
        registerCommand(new MoveCommand());
        registerCommand(new NpCommand());
        registerCommand(new PauseCommand());
        registerCommand(new PlayCommand());
        registerCommand(new QueueCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new SeekCommand());
        registerCommand(new SettingsCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new SkipCommand());
        registerCommand(new StopCommand());
        registerCommand(new VolumeCommand());
        registerCommand(new StatsCommand());
        // if (Lobster.getInstance().isDebugMode()) registerCommand(new DebugCommand());
        this.logger.info("Registered {} commands", this.commands.size());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        BotCommand command = this.commands.get(event.getName());
        CommandContext commandContext = new CommandContext(event);
        command.onCommand(commandContext);
        // try {
        //     GuildSettings guildSettings = settingsManager.getSettings(commandContext.getGuild().getIdLong());
        //     guildSettings.setLastChannelUsedId(event.getChannel().getIdLong());
        //     settingsManager.updateSettings(commandContext.getGuild().getIdLong(), guildSettings);
        // } catch (SQLException e) {
        //     this.logger.error("Could not update lastChannelUsedId", e);
        // }
        this.logger.info("[{}] {}: {}", commandContext.getGuild().getName(), event.getUser().getName(), event.getCommandString());
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        BotCommand commandAutoCompletion = this.commands.get(event.getName());
        event.replyChoices(commandAutoCompletion.onAutocomplete(event)).queue();
    }

    private void registerCommand(BotCommand command) {
        this.commands.putIfAbsent(command.getCommandData().getName(), command);
    }

    public Map<String, BotCommand> getCommands() {
        return this.commands;
    }

    public List<SlashCommandData> getCommandDataList() {
        return this.commands.values().stream()
                .map(BotCommand::getCommandData)
                .map(command -> command.setGuildOnly(true))
                .collect(Collectors.toList());
    }

}
