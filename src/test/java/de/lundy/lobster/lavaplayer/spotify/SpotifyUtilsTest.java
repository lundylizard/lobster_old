package de.lundy.lobster.lavaplayer.spotify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpotifyUtilsTest {

    @Test
    void isSpotifyLink() {
        assertTrue(SpotifyUtils.isSpotifyLink("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2"));
        assertTrue(SpotifyUtils.isSpotifyLink("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2?si=d2171b4a17834f47"));
        assertFalse(SpotifyUtils.isSpotifyLink("https://spotify.com/track/0vf2eBw2inhl8y61cYQMv2?si=d2171b4a17834f47"));
        assertFalse(SpotifyUtils.isSpotifyLink("test"));
    }

    @Test
    void isSpotifyPlaylist() {
        assertTrue(SpotifyUtils.isSpotifyPlaylist("https://open.spotify.com/playlist/37i9dQZEVXbKXQ4mDTEBXq?si=8b884b3a934544c5"));
        assertTrue(SpotifyUtils.isSpotifyPlaylist("https://open.spotify.com/playlist/37i9dQZEVXbKXQ4mDTEBXq"));
        assertFalse(SpotifyUtils.isSpotifyPlaylist("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2"));
        assertFalse(SpotifyUtils.isSpotifyPlaylist("test"));
    }

    @Test
    void getSpotifyIdFromLink() {
        assertEquals("0vf2eBw2inhl8y61cYQMv2", SpotifyUtils.getSpotifyIdFromLink("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2"));
        assertEquals("0vf2eBw2inhl8y61cYQMv2", SpotifyUtils.getSpotifyIdFromLink("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2?si=d2171b4a17834f47"));
        assertEquals("37i9dQZEVXbKXQ4mDTEBXq", SpotifyUtils.getSpotifyIdFromLink("https://open.spotify.com/playlist/37i9dQZEVXbKXQ4mDTEBXq"));
        assertEquals("37i9dQZEVXbKXQ4mDTEBXq", SpotifyUtils.getSpotifyIdFromLink("https://open.spotify.com/playlist/37i9dQZEVXbKXQ4mDTEBXq?si=8b884b3a934544c5"));
    }
}