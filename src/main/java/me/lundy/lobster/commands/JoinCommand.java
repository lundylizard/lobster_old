package me.lundy.lobster.commands;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

@CommandInfo(name = "join", description = "Let lobster join your voice channel")
public class JoinCommand extends Command {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
            return;
        }

        if (context.selfInVoice()) {
            context.getEvent().reply(":warning: I am already in a voice channel.").setEphemeral(true).queue();
            return;
        }

        AudioManager audioManager = context.getGuild().getAudioManager();
        AudioChannel audioChannel = context.getExecutorVoiceState().getChannel();

        if (audioChannel == null) {
            context.getEvent().reply(":warning: Unexpected error: `Could not find voice channel`").setEphemeral(true).queue();
            return;
        }

        try {
            audioManager.openAudioConnection(audioChannel);
        } catch (InsufficientPermissionException e) {
            context.getEvent().reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
        } finally {
            context.getEvent().replyFormat("Joined voice channel %s", "`\uD83D\uDD0A " + audioChannel.getName() + "`").queue();
        }

    }
}
