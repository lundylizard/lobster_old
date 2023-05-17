package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BotUtils {

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

    public static String convertMillisecondsToHoursMinutes(long milliseconds) {
        long minutes = milliseconds / 60000;
        long hours = minutes / 60;
        return String.format("%d hour%s, %d minute%s",
                hours, (hours != 1 ? "s" : ""),
                minutes % 60, (minutes % 60 != 1 ? "s" : ""));
    }

    public static String getTrackPosition(AudioTrack track) {
        long currentPosition = track.getPosition();
        long duration = track.getDuration();
        return formatTime(currentPosition) + " / " + formatTime(duration);
    }


    public static String shortenString(String input, int length) {
        if (input.length() > length) {
            int lastIndex = input.substring(0, length).lastIndexOf(' ');
            if (lastIndex != -1) {
                return input.substring(0, lastIndex) + "...";
            }
            return input.substring(0, length) + "...";
        } else {
            return input;
        }
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
                Lobster.getInstance().getLogger().info("Left voice chat in guild {} due to inactivity.", guild.getName());
            }

        }
    }

    public static String randomFooter() {
        List<String> footers = new ArrayList<>();
        footers.add("\uD83E\uDD9E Made in Germany");
        footers.add("\uD83E\uDD9E Bugs? Suggestions? Join the discord!");
        footers.add("\uD83E\uDD9E Did you know lobsters used to be prison food?");
        footers.add("\uD83E\uDD9E dont settle for imitation");
        footers.add("\uD83E\uDD9E Pepsi Max Jumpscare");
        footers.add("\uD83E\uDD9E lobsters taste better than crabs");
        return footers.get(new Random().nextInt(footers.size()));
    }

    public static int countLetters(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            count += (str.charAt(i) == 'l' || str.charAt(i) == 'i' || str.charAt(i) == 'j') ? 1 : 2;
        }
        return count + str.length();
    }

}
