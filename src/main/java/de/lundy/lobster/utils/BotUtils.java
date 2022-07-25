package de.lundy.lobster.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BotUtils {

    BotUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Formats given millis to hh:mm:ss
     *
     * @param millis Milliseconds to format
     * @return String with millis formatted to hh:mm:ss
     */
    public static String formatTime(long millis) {

        var hours = millis / TimeUnit.HOURS.toMillis(1);
        var minutes = millis / TimeUnit.MINUTES.toMillis(1) % 60;
        var seconds = millis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }

    /**
     * Parse String to Integer if possible
     *
     * @param string String to parse
     * @return Parsed Integer
     */
    public static int parseAsInt(String string) {

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Could not parse string to integer");
        }

    }

    /**
     * Parse String Array to Integer Array if possible
     *
     * @param string String Array to parse
     * @return Parsed Integer Array
     */
    public static int[] parseAsInt(String[] string) {

        try {
            return Arrays.stream(string).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Could not parse string[] to integer[]");
        }

    }

    /**
     * Returns a formatted String of the track's position
     *
     * @param trackPosition Position of track
     * @param trackDuration Duration of track
     * @return Formatted String mm:ss / mm:ss of track position
     */
    public static String getTrackPosition(long trackPosition, long trackDuration) {

        var current = new long[]{(trackPosition / 1000) / 60, (trackPosition / 1000) % 60};
        var duration = new long[]{(trackDuration / 1000) / 60, (trackDuration / 1000) % 60};

        return String.format("%02d:%02d / %02d:%02d", current[0], current[1], duration[0], duration[1]);

    }

    /**
     * @param prefix Replacement for %prefix%
     *
     * @return A random String for the embed footer
     */
    public static String randomFooter(String prefix) {

        var messages = new ArrayList<String>();
        messages.add("\uD83E\uDD9E Do you enjoy this bot? Please share it with your friends: %prefix%invite");
        messages.add("\uD83E\uDD9E Did you know lobsters used to be prison food?");
        messages.add("\uD83E\uDD9E lundy would never eat a lobster.");
        messages.add("\uD83E\uDD9E Shout out to the Lobster Gang!");
        messages.add("\uD83E\uDD9E Found a bug? Have a feature request? Create an issue on GitHub!");
        messages.add("\uD83E\uDD9E Lobsters smell with their legs.");

        return messages.get(new Random().nextInt(messages.size())).replace("%prefix%", prefix);

    }

    public static boolean isRange(String input) {
        return input.matches("\\d+-\\d+");
    }

    public static boolean isValidIndex(int input) {
        return input > 0;
    }

}
