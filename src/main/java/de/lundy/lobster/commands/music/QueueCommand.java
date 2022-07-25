package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class QueueCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        var selfVoiceState = self.getVoiceState();

        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState != null ? selfVoiceState.getChannel() : null)) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var queue = musicManager.scheduler.queue;
        var playingTrack = musicManager.audioPlayer.getPlayingTrack();

        if (queue.isEmpty() && playingTrack == null) {
            channel.sendMessage(":warning: The queue is currently empty and there is no song playing.").queue();
            return;
        }

        if (queue.isEmpty()) {
            var trackInfo = playingTrack.getInfo();
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription(":warning: The queue is currently empty.\n\n" + ":notes: **NOW PLAYING:** " + trackInfo.title + " `by " + trackInfo.author + "` [`" + BotUtils.getTrackPosition(playingTrack.getPosition(), playingTrack.getDuration()) + "`]").setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor()).build()).queue();
            return;
        }

        var trackCount = Math.min(queue.size(), 10);
        var trackList = new ArrayList<>(queue);
        var messageAction = new StringBuilder("**CURRENT QUEUE:**\n\n");
        var trackInfo = musicManager.audioPlayer.getPlayingTrack().getInfo();
        var duration = musicManager.audioPlayer.getPlayingTrack().getDuration();

        messageAction.append("*Current Song: ").append(trackInfo.title).append("* [`").append(BotUtils.formatTime(duration)).append("`]\n\n");

        for (var i = 0; i < trackCount; i++) {

            var track = trackList.get(i);
            var info = track.getInfo();

            messageAction.append('#').append(i + 1).append(" ").append(info.title).append(" `by ").append(info.author).append("` [`").append(BotUtils.formatTime(track.getDuration())).append("`]\n");

        }

        if (trackList.size() > trackCount) {
            messageAction.append("\nand ").append(trackList.size() - trackCount).append(" more...");
        }

        channel.sendMessageEmbeds(new EmbedBuilder().setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor()).setDescription(messageAction.toString()).setFooter(BotUtils.randomFooter("!")).build()).queue();

    }
}
