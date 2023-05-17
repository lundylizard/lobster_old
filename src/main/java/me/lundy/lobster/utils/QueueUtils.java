package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class QueueUtils {

    public static final int TRACKS_PER_PAGE = 15;

    public static AudioTrack[][] splitTracksIntoGroups(BlockingDeque<AudioTrack> audioTracks) {

        // Calculate the total number of pages
        int totalPages = (int) Math.ceil((double) audioTracks.size() / TRACKS_PER_PAGE);

        // Create an array to store the groups of tracks
        List<AudioTrack>[] trackGroups = new List[totalPages];

        // Loop through each track in the deque
        int i = 0;
        for (AudioTrack track : audioTracks) {
            // Calculate the index of the group for this track
            int groupIndex = i / TRACKS_PER_PAGE;

            // If this is the first track for a new group, create the group
            if (trackGroups[groupIndex] == null) {
                trackGroups[groupIndex] = new ArrayList<>();
            }

            // Add the current track to the current group
            trackGroups[groupIndex].add(track);

            // Increment the track count
            i++;
        }

        // Convert each group of tracks to an array
        AudioTrack[][] trackArrays = new AudioTrack[totalPages][];
        for (int j = 0; j < totalPages; j++) {
            List<AudioTrack> group = trackGroups[j];
            AudioTrack[] groupArray = group.toArray(new AudioTrack[0]);
            trackArrays[j] = groupArray;
        }

        return trackArrays;
    }

    public static String generateFooter(int currentPage, int pagesTotal, int queueSize, String totalLengthString) {
        String pageFormat = "Page %d of %d (%d songs total // %s)";
        return String.format(pageFormat, currentPage + 1, pagesTotal, queueSize, totalLengthString);
    }

    public static String generateCurrentTrack(AudioTrack currentTrack) {
        return String.format("\uD83D\uDD0A **%s** from %s `\uD83D\uDD52%s`\n\n", currentTrack.getInfo().title, currentTrack.getUserData().toString(), BotUtils.getTrackPosition(currentTrack));
    }

    public static String generateQueueTrack(int trackIndex, AudioTrack track) {
        String shortenedTitle = BotUtils.shortenString(track.getInfo().title, 22);
        String shortenedArtist = track.getInfo().author;
        return String.format("`#%02d` [%s](%s) | %s `\uD83D\uDD52%s`", trackIndex, shortenedTitle, track.getInfo().uri, shortenedArtist, BotUtils.formatTime(track.getDuration()));
    }

    public static List<Button> generateButtons(int pagesTotal, int currentPage) {
        return List.of(
                Button.secondary("button:queue:page:first", Emoji.fromUnicode("⏪")).withDisabled(currentPage == 0),
                Button.secondary("button:queue:previous:" + (currentPage - 1), Emoji.fromUnicode("◀️")).withDisabled(currentPage == 0),
                Button.secondary("button:queue:next:" + (currentPage + 1), Emoji.fromUnicode("▶️")).withDisabled(currentPage + 1 == pagesTotal),
                Button.secondary("button:queue:next:last", Emoji.fromUnicode("⏩")).withDisabled(currentPage + 1 == pagesTotal)
        );
    }

}
