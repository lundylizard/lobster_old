package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SkipCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        if (!(selfVoiceState != null && selfVoiceState.inAudioChannel())) {
            channel.sendMessage(":warning: I am not playing anything.").queue();
            return;
        }

        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
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

        if (args.length == 1) {

            var amount = BotUtils.parseAsInt(args[0]);
            var queueSize = musicManager.scheduler.queue.size();

            if (amount > queueSize) {
                channel.sendMessage(":warning: The queue is " + queueSize + " songs long. You cannot skip more than that.").queue();
                return;
            }

            for (var i = 0; i < amount; i++) {
                musicManager.scheduler.nextTrack();
            }

            channel.sendMessage("Skipped `" + amount + "` songs from the queue.").queue();

        } else {

            var trackInfo = audioPlayer.getPlayingTrack().getInfo();
            channel.sendMessage("Skipped `" + trackInfo.title + "`").queue();
            musicManager.scheduler.nextTrack();

        }

    }
}


