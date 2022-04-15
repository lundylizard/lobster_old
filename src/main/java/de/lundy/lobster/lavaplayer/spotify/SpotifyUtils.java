package de.lundy.lobster.lavaplayer.spotify;

import org.jetbrains.annotations.NotNull;

public class SpotifyUtils {

    SpotifyUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isSpotifyLink(@NotNull String link) {
        return link.toLowerCase().startsWith("https://open.spotify.com/");
    }

    public static boolean isSpotifyPlaylist(@NotNull String link) {
        return link.replace("\\?si=.*$", "").replace("https://open.spotify.com/", "").startsWith("playlist/");
    }

    public static @NotNull String getSpotifyIdFromLink(@NotNull String link) {
        return link.replaceAll("\\?si=.*$", "").replace("https://open.spotify.com/" + (isSpotifyPlaylist(link) ? "playlist/" : "track/"), "");
    }

}
