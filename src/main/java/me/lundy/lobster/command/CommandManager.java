package me.lundy.lobster.command;

import me.lundy.lobster.commands.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private boolean updated = false;

    public CommandManager() {
        registerCommand(new HelpCommand());
        registerCommand(new LoopCommand());
        registerCommand(new LyricsCommand());
        registerCommand(new MoveCommand());
        registerCommand(new NpCommand());
        registerCommand(new PauseCommand());
        registerCommand(new PlayCommand());
        registerCommand(new QueueCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new SeekCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new SkipCommand());
        registerCommand(new StopCommand());
        registerCommand(new VolumeCommand());
        logger.info("Registered {} commands", commands.size());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Checks.notNull(event.getGuild(), "Guild");
        BotCommand command = commands.get(event.getName());
        command.execute(event);
        logger.info("[{}] {}: {}", event.getGuild().getName(), event.getUser().getName(), event.getCommandString());
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        BotCommand commandAutoCompletion = commands.get(event.getName());
        event.replyChoices(commandAutoCompletion.onAutocomplete(event)).queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        if (updated) return;
        event.getJDA().updateCommands().addCommands(getCommandDataList()).queue();
        updated = true;
    }

    private void registerCommand(BotCommand command) {
        commands.putIfAbsent(command.getCommandData().getName(), command);
    }

    private List<SlashCommandData> getCommandDataList() {
        return commands.values().stream()
                .map(BotCommand::getCommandData)
                .peek(command -> command.setGuildOnly(true))
                .collect(Collectors.toList());
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }

}
