package me.lundy.lobster.command;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.checks.CheckHandler;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.commands.slash.misc.HelpCommand;
import me.lundy.lobster.commands.slash.misc.InviteCommand;
import me.lundy.lobster.commands.slash.music.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, Command> commands = new HashMap<>();
    Logger logger = LoggerFactory.getLogger(CommandManager.class);

    public CommandManager() {
        registerCommand(new HelpCommand());
        registerCommand(new InviteCommand());
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
        logger.info("Registered all commands!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply(":warning: Unexpected error: GUILD_IS_NULL").setEphemeral(true).queue();
            return;
        }

        Member executor = event.getMember();
        if (executor == null) {
            event.reply(":warning: Unexpected error: EXECUTOR_IS_NULL").setEphemeral(true).queue();
            return;
        }

        Command command = this.commands.get(event.getName());
        if (command == null) {
            event.reply(":warning: Unexpected error: COMMAND_IS_NULL").setEphemeral(true).queue();
            return;
        }

        Method onCommand;

        try {
            onCommand = command.getClass().getMethod("onCommand", SlashCommandInteractionEvent.class);
        } catch (NoSuchMethodException e) {
            logger.error("There is no onCommand() method in " + command.getClass().getName(), e);
            event.reply(":warning: Unexpected error: COMMAND_METHOD_NOT_FOUND").setEphemeral(true).queue();
            return;
        }

        if (onCommand.isAnnotationPresent(RunCheck.class)) {
            CommandCheck commandCheck = onCommand.getAnnotation(RunCheck.class).check();
            if (!CheckHandler.runCheck(commandCheck, guild.getSelfMember(), executor)) {
                Button helpButton = Button.secondary("help:sameVoice", Emoji.fromUnicode("❔"));
                event.reply(commandCheck.getFailMessage()).setActionRow(helpButton).setEphemeral(true).queue();
                return;
            }
        }

        command.onCommand(event);
        logger.info("({}) {} -> {}", guild.getName(), executor.getUser().getAsTag(), event.getCommandString());
        Lobster.getInstance().getChannelCollector().addTextChannel(event.getChannel().asTextChannel());

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
        if (commandInfo == null) throw new IllegalArgumentException(command.getClass().getName() + " is missing CommandInfo annotation");
        return Commands.slash(commandInfo.name(), commandInfo.description())
                .setGuildOnly(true)
                .addOptions(command instanceof CommandOptions ? ((CommandOptions) command).options() : Collections.emptyList());
    }

}
