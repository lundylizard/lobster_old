package me.lundy.lobster.command;

import me.lundy.lobster.commands.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, Command> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
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
        registerCommand(new ShuffleCommand());
        registerCommand(new SkipCommand());
        registerCommand(new StopCommand());
        registerCommand(new VolumeCommand());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = this.commands.get(event.getName());

        if (command == null) {
            event.reply(":warning: Unexpected error: `Command is null`").setEphemeral(true).queue();
            return;
        }

        CommandContext commandContext = new CommandContext(event);
        command.onCommand(commandContext);
    }

    private void registerCommand(Command command) {
        CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);

        if (commandInfo == null) {
            throw new IllegalArgumentException(command.getClass().getName() + " is missing CommandInfo annotation");
        }

        commands.putIfAbsent(commandInfo.name(), command);
    }

    public Map<String, Command> getCommands() {
        return this.commands;
    }

    public List<SlashCommandData> getCommandDataList() {
        return this.commands.values().stream().map(this::createCommandData).collect(Collectors.toList());
    }

    private SlashCommandData createCommandData(Command command) {
        CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);

        if (commandInfo == null) {
            throw new IllegalArgumentException(command.getClass().getName() + " is missing CommandInfo annotation");
        }

        return Commands.slash(commandInfo.name(), commandInfo.description())
                .setGuildOnly(true)
                .addOptions(command instanceof CommandOptions ? ((CommandOptions) command).options() : Collections.emptyList());
    }
}
