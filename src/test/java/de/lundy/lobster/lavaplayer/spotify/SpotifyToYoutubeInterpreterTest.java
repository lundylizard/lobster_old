package de.lundy.lobster.lavaplayer.spotify;

import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpotifyToYoutubeInterpreterTest {

    private final SpotifyToYoutubeInterpreter spotify = new SpotifyToYoutubeInterpreter();

    @Test
    void getArtistFromSpotify() {

        try {
            assertEquals("Cavetown", spotify.getArtistFromSpotify(SpotifyUtils.getSpotifyIdFromLink("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2")));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void getSongNameFromSpotify() {

        try {
            assertEquals("Devil Town", spotify.getSongNameFromSpotify(SpotifyUtils.getSpotifyIdFromLink("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2")));
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }

    }

}