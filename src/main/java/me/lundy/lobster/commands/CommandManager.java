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

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final List<BotCommand> commandList;
    private final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    public CommandManager() {
        this.commandList = new ArrayList<>();
        registerCommand(new HelpCommand());
        registerCommand(new InviteCommand());
        registerCommand(new LeaveCommand());
        registerCommand(new LoopCommand());
        registerCommand(new MoveCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new PauseCommand());
        registerCommand(new PlayCommand());
        registerCommand(new QueueCommand());
        registerCommand(new RemoveCommand());
        registerCommand(new SeekCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new SkipCommand());
        registerCommand(new StopCommand());
        registerCommand(new VolumeCommand());
        this.logger.info("Registered all commands!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        for (BotCommand command : this.commandList) {

            if (command.name().equals(event.getName())) {
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
        }
    }

    public void registerCommand(BotCommand command) {
        this.commandList.add(command);
    }

    public List<BotCommand> getCommands() {
        return this.commandList;
    }

    public List<SlashCommandData> getCommandDataList() {

        List<SlashCommandData> commandDataList = new ArrayList<>();

        for (BotCommand command : this.commandList) {

            SlashCommandData commandData = Commands.slash(command.name(), command.description());

            if (!command.options().isEmpty()) {
                commandData.addOptions(command.options());
            }

            if (!command.subCommands().isEmpty()) {
                commandData.addSubcommands(command.subCommands());
            }

            commandData.setGuildOnly(true);
            commandDataList.add(commandData);
        }

        return commandDataList;

    }

}
