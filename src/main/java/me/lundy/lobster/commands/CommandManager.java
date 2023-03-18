package me.lundy.lobster.commands;

import me.lundy.lobster.commands.impl.misc.HelpCommand;
import me.lundy.lobster.commands.impl.misc.InviteCommand;
import me.lundy.lobster.commands.impl.music.*;
import me.lundy.lobster.utils.VoiceChatCheck;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, BotCommand> commandMap = new HashMap<>();

    public CommandManager() {
        registerCommand(new HelpCommand());
        registerCommand(new InviteCommand());
        registerCommand(new JoinCommand());
        registerCommand(new LeaveCommand());
        registerCommand(new LoopCommand());
        registerCommand(new LyricsCommand());
        registerCommand(new MoveCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new PlayCommand());
        registerCommand(new QueueCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new SeekCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new SkipCommand());
        registerCommand(new StopCommand());
        registerCommand(new VolumeCommand());
        Logger logger = LoggerFactory.getLogger(CommandManager.class);
        logger.info("Registered all commands!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        BotCommand command = this.commandMap.get(event.getName());
        if (command == null) return;

        if (!command.getClass().isAnnotationPresent(IgnoreChecks.class)) {

            VoiceChatCheck.CheckResult voiceChatCheck = VoiceChatCheck.runCheck(event.getHook());

            if (!voiceChatCheck.hasPassed()) {
                event.reply(voiceChatCheck.getMessage()).setEphemeral(true).queue();
                return;
            }

        }

        command.onCommand(event);
        command.getLogger().info("({}) {} -> {}", event.getGuild().getName(), event.getMember().getUser().getAsTag(), event.getCommandString());

    }

    public void registerCommand(BotCommand command) {
        this.commandMap.put(command.name(), command);
    }

    public Map<String, BotCommand> getCommands() {
        return this.commandMap;
    }

    public List<SlashCommandData> getCommandDataList() {
        return this.commandMap.values().stream().map(this::createCommandData).collect(Collectors.toList());
    }

    private SlashCommandData createCommandData(BotCommand command) {
        return Commands.slash(command.name(), command.description())
                .addOptions(command.options())
                .addSubcommands(command.subCommands())
                .setGuildOnly(true);
    }

}
