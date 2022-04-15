package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JoinCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        if (selfVoiceState != null && selfVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: I'm already in a voice channel.").queue();
            return;
        }

        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        if (! (memberVoiceState != null && memberVoiceState.inVoiceChannel())) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        var memberChannel = memberVoiceState.getChannel();
        var audioManager = event.getGuild().getAudioManager();

        try {
            audioManager.openAudioConnection(memberChannel);
        } catch (InsufficientPermissionException e) {
            event.getChannel().sendMessage(":warning: I do not have enough permissions to join that channel.").queue();
            return;
        }

        channel.sendMessage(":loud_sound: Connecting to `\uD83D\uDD0A " + Objects.requireNonNull(memberChannel).getName() + "`").queue();

    }
}