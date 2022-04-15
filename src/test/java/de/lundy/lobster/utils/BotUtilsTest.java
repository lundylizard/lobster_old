package de.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotUtilsTest {

    @org.junit.jupiter.api.Test
    void formatTime() {
        assertEquals("00:00:01", BotUtils.formatTime(1000));
        assertEquals("00:00:10", BotUtils.formatTime(10000));
        assertEquals("00:01:00", BotUtils.formatTime(60000));
        assertEquals("00:10:00", BotUtils.formatTime(600000));
        assertEquals("01:00:00", BotUtils.formatTime(3600000));
        assertEquals("10:00:00", BotUtils.formatTime(36000000));
    }

    @org.junit.jupiter.api.Test
    void testParseAsInt() {
        assertEquals(100, BotUtils.parseAsInt("100"));
        assertEquals(- 1, BotUtils.parseAsInt("-1"));
        assertThrows(NumberFormatException.class, () -> BotUtils.parseAsInt("test"));
    }

    @org.junit.jupiter.api.Test
    void testParseAsIntArray() {
        assertArrayEquals(new int[]{1, 2, 3}, BotUtils.parseAsInt(new String[]{"1", "2", "3"}));
        assertNotEquals(new int[]{1, 2, 3}, BotUtils.parseAsInt(new String[]{"4", "5", "6"}));
        assertThrows(NumberFormatException.class, () -> BotUtils.parseAsInt(new String[]{"t", "e", "s", "t"}));
    }

    @org.junit.jupiter.api.Test
    void getTrackPosition() {

        AudioTrack mockTrack = new AudioTrack() {

            @Override
            public AudioTrackInfo getInfo() {
                return null;
            }

            @Override
            public String getIdentifier() {
                return null;
            }

            @Override
            public AudioTrackState getState() {
                return null;
            }

            @Override
            public void stop() {

            }

            @Override
            public boolean isSeekable() {
                return false;
            }

            @Override
            public long getPosition() {
                return 13000;
            }

            @Override
            public void setMarker(TrackMarker trackMarker) {

            }

            @Override
            public void setPosition(long l) {

            }

            @Override
            public long getDuration() {
                return 60000;
            }

            @Override
            public AudioTrack makeClone() {
                return null;
            }

            @Override
            public AudioSourceManager getSourceManager() {
                return null;
            }

            @Override
            public Object getUserData() {
                return null;
            }

            @Override
            public <T> T getUserData(Class<T> aClass) {
                return null;
            }

            @Override
            public void setUserData(Object o) {

            }


        };
        AudioTrack mockTrack2 = new AudioTrack() {

            @Override
            public AudioTrackInfo getInfo() {
                return null;
            }

            @Override
            public String getIdentifier() {
                return null;
            }

            @Override
            public AudioTrackState getState() {
                return null;
            }

            @Override
            public void stop() {

            }

            @Override
            public boolean isSeekable() {
                return false;
            }

            @Override
            public long getPosition() {
                return 2345;
            }

            @Override
            public void setMarker(TrackMarker trackMarker) {

            }

            @Override
            public void setPosition(long l) {

            }

            @Override
            public long getDuration() {
                return 265654;
            }

            @Override
            public AudioTrack makeClone() {
                return null;
            }

            @Override
            public AudioSourceManager getSourceManager() {
                return null;
            }

            @Override
            public Object getUserData() {
                return null;
            }

            @Override
            public <T> T getUserData(Class<T> aClass) {
                return null;
            }

            @Override
            public void setUserData(Object o) {

            }


        };

        assertEquals("00:13 / 01:00", BotUtils.getTrackPosition(mockTrack.getPosition(), mockTrack.getDuration()));
        assertEquals("00:01 / 01:09", BotUtils.getTrackPosition(1000, 69000));
        assertNotEquals("00:13 / 1:09", BotUtils.getTrackPosition(mockTrack2.getPosition(), mockTrack2.getDuration()));
        assertNotEquals("00:01 / 01:09", BotUtils.getTrackPosition(8723, 32423));

    }

    @Test
    void isUrl() {
        assertTrue(BotUtils.isUrl("https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2"));
        assertFalse(BotUtils.isUrl("test"));
        assertFalse(BotUtils.isUrl("open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2"));
    }

    @Test
    void isRange() {

        for (var i = 1; i < 100; i++) {

            assertTrue(BotUtils.isRange("1-" + i));
            assertTrue(BotUtils.isRange(i + "-1"));
            assertFalse(BotUtils.isRange("a-" + i));
            assertFalse(BotUtils.isRange(i + "-a"));
            assertFalse(BotUtils.isRange(String.valueOf(i)));

            for (var j = 1; j < 100; j++) {
                assertTrue(BotUtils.isRange(i + "-" + j));
            }

        }

        assertFalse(BotUtils.isRange("a-c"));
        assertFalse(BotUtils.isRange("test"));

    }

    @Test
    void isValidIndex() {

        for (var i = 1; i < 100; i++) {
            assertTrue(BotUtils.isValidIndex(i));
        }

        for (var j = 0; j > - 100; j--) {
            assertFalse(BotUtils.isValidIndex(j));
        }

    }
}