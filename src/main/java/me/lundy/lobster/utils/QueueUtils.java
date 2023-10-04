package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.lavaplayer.AudioTrackUserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class QueueUtils {

    public static final int TRACKS_PER_PAGE = 15;

    public static String generateFooter(Pagination<AudioTrack> pagination) {
        long totalLength = pagination.getAllItems().stream().mapToLong(AudioTrack::getDuration).sum();
        return String.format(
                "Page %d of %d (%d songs total | %s)",
                pagination.getCurrentPage(),
                pagination.getTotalPages(),
                pagination.getAllItems().size(),
                StringUtils.convertMsToHoursAndMinutes(totalLength)
        );
    }

    public static String generateCurrentTrack(AudioTrack currentTrack) {
        return String.format("\uD83D\uDD0A **%s** from %s `\uD83D\uDD52%s`", currentTrack.getInfo().title, ((AudioTrackUserData) currentTrack.getUserData()).getUserMention(), StringUtils.getTrackPosition(currentTrack));
    }

    public static String generateQueueTrack(int trackIndex, AudioTrack track) {
        String shortenedTitle = StringUtils.shortenString(track.getInfo().title, 22);
        String shortenedArtist = track.getInfo().author;
        return String.format("`#%02d` [%s](%s) | %s `\uD83D\uDD52%s`", trackIndex, shortenedTitle, track.getInfo().uri, shortenedArtist, StringUtils.formatTime(track.getDuration()));
    }

    public static EmbedBuilder generateEmbedFromCurrentPage(Pagination<AudioTrack> pagination, AudioTrack currentTrack) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Current Queue");
        embedBuilder.setFooter(generateFooter(pagination));
        if (currentTrack != null) embedBuilder.appendDescription(QueueUtils.generateCurrentTrack(currentTrack) + "\n\n");
        List<AudioTrack> tracks = pagination.getCurrentPageItems();
        int trackIndex = QueueUtils.TRACKS_PER_PAGE * (pagination.getCurrentPage() - 1);
        for (AudioTrack track : tracks) {
            trackIndex++;
            String queueTrackString = QueueUtils.generateQueueTrack(trackIndex, track);
            embedBuilder.appendDescription(queueTrackString).appendDescription("\n");
        }
        return embedBuilder;
    }

    public static List<Button> generateButtons(Pagination<AudioTrack> pagination) {
        return List.of(
                Button.secondary("button:queue:first", Emoji.fromUnicode("⏪")).withDisabled(pagination.isFirstPage()),
                Button.secondary("button:queue:previous", Emoji.fromUnicode("◀️")).withDisabled(pagination.isFirstPage()),
                Button.secondary("button:queue:next", Emoji.fromUnicode("▶️")).withDisabled(pagination.isLastPage()),
                Button.secondary("button:queue:last", Emoji.fromUnicode("⏩")).withDisabled(pagination.isLastPage())
        );
    }

}
