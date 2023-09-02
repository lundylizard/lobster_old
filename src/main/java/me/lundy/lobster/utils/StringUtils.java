package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.utils.SplitUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static int timeToSeconds(String time) {
        String[] parts = time.split(":");
        int length = parts.length;

        if (length == 2) { // Format: mm:ss
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return minutes * 60 + seconds;
        } else if (length == 3) { // Format: hh:mm:ss
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            return hours * 3600 + minutes * 60 + seconds;
        } else {
            throw new IllegalArgumentException("Invalid time format. Use either 'hh:mm:ss' or 'mm:ss'.");
        }
    }

    public static boolean isValidTimeFormat(String time) {
        String regex = "^(\\d+:)?[0-5]?\\d:[0-5]?\\d$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    public static String formatTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return (hours > 0) ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static String[] getAvailableTimes(long maxMilliseconds) {
        List<String> times = new ArrayList<>();

        for (long milliseconds = 0; milliseconds <= maxMilliseconds; milliseconds += 30000) {
            String time = formatTime(milliseconds);
            times.add(time);
        }

        times.add(formatTime(maxMilliseconds));

        return times.toArray(new String[0]);
    }

    public static String convertMsToHoursAndMinutes(long milliseconds) {
        long minutes = milliseconds / 60000;
        long hours = minutes / 60;
        return String.format("%d hour%s, %d minute%s", hours, pluralize(hours), minutes % 60, pluralize(minutes % 60));
    }

    public static String getTrackPosition(AudioTrack track) {
        long currentPosition = track.getPosition();
        long duration = track.getDuration();
        return formatTime(currentPosition) + " / " + formatTime(duration);
    }

    public static String shortenString(String input, int length) {
        if (input.length() <= length) return input;
        int lastIndex = input.substring(0, length).lastIndexOf(' ');
        if (lastIndex != -1) return input.substring(0, lastIndex) + "...";
        return input.substring(0, length) + "...";
    }

    public static int countLetters(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isNarrowCharacter(c)) {
                count++;
            } else {
                count += 2;
            }
        }
        return count + str.length();
    }

    private static String pluralize(long value) {
        return value != 1 ? "s" : "";
    }

    private static boolean isNarrowCharacter(char c) {
        return c == 'l' || c == 'i' || c == 'j';
    }

    public static SplitUtil.Strategy onTwoNewLinesStrategy() {
        return SplitUtil.Strategy.onChar(new Predicate<>() {
            private int newLineCount = 0;
            @Override
            public boolean test(Character c) {
                boolean isTwoNewLines = (c == '\n') && (++newLineCount == 2);
                newLineCount = (c == '\n') ? 0 : newLineCount;
                return isTwoNewLines;
            }
        });
    }

}
