package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.utils.SplitUtil;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String formatTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return (hours > 0) ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
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
        Predicate<Character> twoNewLinesPredicate = new Predicate<>() {
            private boolean isNewLine = false;

            @Override
            public boolean test(Character c) {
                if (c == '\n') {
                    if (isNewLine) {
                        isNewLine = false;
                        return true;
                    }
                    isNewLine = true;
                } else {
                    isNewLine = false;
                }

                return false;
            }
        };

        return SplitUtil.Strategy.onChar(twoNewLinesPredicate);
    }

    public static String sanitizeTrackTitle(String trackTitle) {
        Pattern pattern = Pattern.compile("\\b(official|song|lyrics|music|video|\\(|\\)|\\-|ft\\.|feat\\.|\\[|\\]|\\.|\\(|\\))", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trackTitle);
        return matcher.replaceAll("");
    }

}
