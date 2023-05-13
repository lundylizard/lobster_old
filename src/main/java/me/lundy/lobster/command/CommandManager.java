package me.lundy.lobster.command;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.commands.slash.misc.HelpCommand;
import me.lundy.lobster.commands.slash.misc.InviteCommand;
import me.lundy.lobster.commands.slash.music.*;
import me.lundy.lobster.utils.VoiceChatCheck;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (guild == null) return;
        Member executor = event.getMember();
        if (executor == null) return;

        Command command = this.commands.get(event.getName());
        if (command == null) return;

        try {

            if (!command.getClass().getMethod("onCommand", SlashCommandInteractionEvent.class).isAnnotationPresent(IgnoreChecks.class)) {

                VoiceChatCheck.CheckResult voiceChatCheck = VoiceChatCheck.runCheck(event.getHook());

                if (!voiceChatCheck.hasPassed()) {
                    event.reply(voiceChatCheck.getMessage()).setEphemeral(true).queue();
                    return;
                }

            }

        } catch (NoSuchMethodException e) {
            logger.error("There is no onCommand() method in " + command.getClass().getName(), e);
        }

        handleCommand(event);
        logger.info("({}) {} -> {}", guild.getName(), executor.getUser().getAsTag(), event.getCommandString());
        Lobster.getInstance().getChannelCollector().addTextChannel(event.getChannel().asTextChannel());

    }

    private void handleCommand(SlashCommandInteractionEvent event) {
        Command command = commands.get(event.getName());
        if (command == null) return;
        command.onCommand(event);
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
