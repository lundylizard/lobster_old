package de.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ChatUtils {

    //Formats the milliseconds to a hh:mm:ss format
    public static String formatTime(long timeInMillis) {

        var hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        var minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        var seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }

    //Returns a time embed string for discord for the uptime of the bot
    public static @NotNull String getBotUptime() {

        var duration = (System.currentTimeMillis() / 1000) - (ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        return "<t:" + duration + ":R>";

    }

    //Checks if string is number using regex
    public static boolean checkIfNumber(@NotNull String string) {
        return string.matches("[0-9]+");
    }

    //Print function for this bot to print with a time stamp into console
    public static void print(String out) {

        var dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        var time = LocalDateTime.now();
        System.out.println("[" + dateTimeFormatter.format(time) + "] " + out);

    }

    //Returns a formatted string with the track position, I am aware this looks ugly and could be done better
    public static @NotNull String trackPosition(@NotNull AudioTrack track) {

        var position = track.getPosition();
        var duration = track.getDuration();
        var current = new Long[]{(position / 1000) / 60, (position / 1000) % 60};
        var durationSong = new Long[]{(duration / 1000) / 60, (duration / 1000) % 60};

        return (current[0].toString().length() == 1 ? "0" + current[0] : current[0]) + ":" + (current[1].toString().length() == 1 ? "0" + current[1] : current[1])
                + " / " + (durationSong[0].toString().length() == 1 ? "0" + durationSong[0] : durationSong[0]) + ":" + (durationSong[1].toString().length() == 1 ? "0" + durationSong[1] : durationSong[1]);

    }

}
