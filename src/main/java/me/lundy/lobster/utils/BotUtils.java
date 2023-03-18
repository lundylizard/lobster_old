package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public class BotUtils {

    private BotUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatTime(long millis) {

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }

    }

    public static String getTrackPosition(AudioTrack track) {
        long currentPosition = track.getPosition();
        long duration = track.getDuration();
        return formatTime(currentPosition) + " / " + formatTime(duration);
    }


    public static String endStringWithEllipsis(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length) + "...";
        } else {
            return input;
        }
    }

    public static AudioTrack[][] splitTracksIntoGroups(BlockingDeque<AudioTrack> audioTracks) {

        // Calculate the total number of pages
        int totalPages = (int) Math.ceil((double) audioTracks.size() / 15);

        // Create an array to store the groups of tracks
        List<AudioTrack>[] trackGroups = new List[totalPages];

        // Loop through each track in the deque
        int i = 0;
        for (AudioTrack track : audioTracks) {
            // Calculate the index of the group for this track
            int groupIndex = i / 15;

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

    public static void handleInactivity(Guild guild) {

        if (guild.getAudioManager().isConnected()) {

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
            AudioPlayer audioPlayer = musicManager.audioPlayer;

            if (guild.getAudioManager().getConnectedChannel().getMembers().stream()
                    .noneMatch(member -> !member.getVoiceState().isDeafened() && !member.getUser().isBot())) {
                guild.getAudioManager().closeAudioConnection();
                audioPlayer.stopTrack();
                musicManager.scheduler.queue.clear();
                Lobster.getInstance().getLogger().info("Left voice chat in {} due to inactivity.", guild.getName());
            }

        }
    }

}
