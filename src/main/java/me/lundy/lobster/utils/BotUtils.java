package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.TimeUnit;

public class BotUtils {

    private BotUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getTrackPosition(AudioTrack track) {
        long currentPosition = track.getPosition() / 1000;
        long currentMinutes = currentPosition / 60;
        long currentSeconds = currentPosition % 60;
        long duration = track.getDuration() / 1000;
        long durationMinutes = duration / 60;
        long durationSeconds = duration % 60;
        return String.format("%02d:%02d / %02d:%02d", currentMinutes, currentSeconds, durationMinutes, durationSeconds);
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
