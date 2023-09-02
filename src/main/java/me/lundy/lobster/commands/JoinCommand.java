package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(Reply.EXECUTOR_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        if (context.selfInVoice()) {
            context.getEvent().reply(Reply.SELF_ALREADY_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        AudioManager audioManager = context.getGuild().getAudioManager();
        AudioChannel audioChannel = context.getExecutorVoiceState().getChannel();

        if (audioChannel == null) {
            context.getEvent().reply(Reply.ERROR_VOICE_CHANNEL_NULL.getMessage()).setEphemeral(true).queue();
            return;
        }

        try {
            audioManager.openAudioConnection(audioChannel);
        } catch (InsufficientPermissionException e) {
            context.getEvent().reply(Reply.ERROR_NO_PERMISSIONS_VOICE.getMessage()).setEphemeral(true).queue();
        } finally {
            context.getEvent().replyFormat(Reply.JOINED_VOICE.getMessage(), audioChannel.getName()).queue();
        }

    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("join", "Make lobster join your voice channel");
    }
}
