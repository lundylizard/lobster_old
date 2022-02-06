package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.ChatUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SeekCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        assert selfVoiceState != null;
        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: I am not playing anything.").queue();
            return;
        }

        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage(":warning: There is currently no track playing.").queue();
            return;
        }

        if (ChatUtils.checkIfNumber(args[0].split(":")[0])) {
            channel.sendMessage(":warning: `" + args[0] + "` is not a valid timestamp.").queue();
            return;
        }

        if (ChatUtils.checkIfNumber(args[0].split(":")[1])) {
            channel.sendMessage(":warning: `" + args[0] + "` is not a valid timestamp.").queue();
            return;
        }

        var mins = Integer.parseInt(args[0].split(":")[0]);
        var secs = Integer.parseInt(args[0].split(":")[1]);

        if (secs > 60 || secs < 0) {
            channel.sendMessage(":warning: `" + args[0] + "` is not a valid timestamp.").queue();
            return;
        }

        if (mins < 0) {
            channel.sendMessage(":warning: `" + args[0] + "` is not a valid timestamp.").queue();
            return;
        }

        var seekPos = mins * 60000L + secs * 1000L;

        if (seekPos > audioPlayer.getPlayingTrack().getDuration()) {
            channel.sendMessage(":warning: Could not skip to `" + (String.valueOf(secs).length() == 0 ? args[0].split(":")[1] + "0" : args[0].split(":")[1]) + "`").queue();
            return;
        }

        audioPlayer.getPlayingTrack().setPosition(seekPos);
        channel.sendMessage(":fast_forward: Set song position to `" + mins + ":" + (String.valueOf(secs).length() == 0 ? args[0].split(":")[1] + "0" : args[0].split(":")[1]) + "`").queue();

    }
}