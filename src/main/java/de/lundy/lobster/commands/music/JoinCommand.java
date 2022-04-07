package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JoinCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        try {

            var channel = event.getTextChannel();
            var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
            var selfVoiceState = self.getVoiceState();

            assert selfVoiceState != null;
            if (selfVoiceState.inVoiceChannel()) {
                channel.sendMessage(":warning: I'm already in a voice channel.").queue();
                return;
            }

            var member = event.getMember();
            var memberVoiceState = member.getVoiceState();

            assert memberVoiceState != null;
            if (!memberVoiceState.inVoiceChannel()) {
                channel.sendMessage(":warning: You are not in a voice channel.").queue();
                return;
            }

            var audioManager = event.getGuild().getAudioManager();
            var memberChannel = memberVoiceState.getChannel();
            audioManager.openAudioConnection(memberChannel);
            assert memberChannel != null;
            channel.sendMessage(":loud_sound: Connecting to `\uD83D\uDD0A " + memberChannel.getName() + "`").queue();

        } catch (InsufficientPermissionException e) {
            event.getChannel().sendMessage(":warning: I do not have enough permissions to join that channel.").queue();
            e.printStackTrace();
        }

    }
}