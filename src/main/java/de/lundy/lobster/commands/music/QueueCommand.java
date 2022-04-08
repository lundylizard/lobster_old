package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class QueueCommand implements Command {

    private final SettingsManager settingsManager;

    public QueueCommand(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var queue = musicManager.scheduler.queue;
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();
        var selfVoiceState = self.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        assert selfVoiceState != null;
        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        if (queue.isEmpty() && musicManager.audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage(":warning: The queue is currently empty and there is no song playing.").queue();
            return;
        }

        if (queue.isEmpty() && musicManager.audioPlayer.getPlayingTrack() != null) {

            var trackInfo = musicManager.audioPlayer.getPlayingTrack().getInfo();
            channel.sendMessage(new EmbedBuilder()
                    .setDescription(":warning: The queue is currently empty.\n\n:notes: **NOW PLAYING:** " + trackInfo.title + " `by " + trackInfo.author + "` [`" + ChatUtils.trackPosition(musicManager.audioPlayer.getPlayingTrack()) + "`]")
                    .setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor())
                    .build()).queue();
            return;

        }

        var trackCount = Math.min(queue.size(), 10);
        var trackList = new ArrayList<>(queue);
        var messageAction = new StringBuilder("**CURRENT QUEUE:**\n\n");
        var trackInfo = musicManager.audioPlayer.getPlayingTrack().getInfo();

        messageAction.append("*Current Song: ")
                .append(trackInfo.title)
                .append("* [`").append(ChatUtils.formatTime(musicManager.audioPlayer.getPlayingTrack().getDuration()))
                .append("`]\n\n");

        for (var i = 0; i < trackCount; i++) {

            var track = trackList.get(i);
            var info = track.getInfo();

            messageAction.append('#')
                    .append(i + 1)
                    .append(" ")
                    .append(info.title)
                    .append(" `by ")
                        .append(info.author)
                        .append("` [`")
                        .append(ChatUtils.formatTime(track.getDuration()))
                    .append("`]\n");

        }

        if (trackList.size() > trackCount) {
            messageAction.append("\nand ")
                    .append(trackList.size() - trackCount)
                    .append(" more...");
        }

        channel.sendMessage(new EmbedBuilder()
                .setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor())
                .setDescription(messageAction.toString())
                .setFooter(ChatUtils.randomFooter(event.getGuild().getIdLong(), settingsManager))
                .build()).queue();

    }
}
