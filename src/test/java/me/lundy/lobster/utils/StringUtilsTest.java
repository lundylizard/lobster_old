package me.lundy.lobster.utils;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @org.junit.jupiter.api.Test
    void timeToSeconds() {
        assertEquals(1, StringUtils.timeToSeconds("00:01"));
        assertEquals(10, StringUtils.timeToSeconds("00:10"));
        assertEquals(11, StringUtils.timeToSeconds("00:11"));
        assertEquals(60, StringUtils.timeToSeconds("01:00"));
        assertEquals(61, StringUtils.timeToSeconds("01:01"));
        assertEquals(70, StringUtils.timeToSeconds("01:10"));
        assertEquals(71, StringUtils.timeToSeconds("01:11"));
        assertEquals(3600, StringUtils.timeToSeconds("01:00:00"));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.timeToSeconds("no time"));
    }

    @org.junit.jupiter.api.Test
    void isValidTimeFormat() {
        assertTrue(StringUtils.isValidTimeFormat("00:01"));
        assertTrue(StringUtils.isValidTimeFormat("00:10"));
        assertTrue(StringUtils.isValidTimeFormat("00:11"));
        assertTrue(StringUtils.isValidTimeFormat("01:00"));
        assertTrue(StringUtils.isValidTimeFormat("01:01"));
        assertTrue(StringUtils.isValidTimeFormat("01:11"));
        assertTrue(StringUtils.isValidTimeFormat("10:00"));
        assertTrue(StringUtils.isValidTimeFormat("10:01"));
        assertTrue(StringUtils.isValidTimeFormat("10:10"));
        assertTrue(StringUtils.isValidTimeFormat("10:11"));
        assertTrue(StringUtils.isValidTimeFormat("11:00"));
        assertTrue(StringUtils.isValidTimeFormat("11:01"));
        assertTrue(StringUtils.isValidTimeFormat("11:10"));
        assertTrue(StringUtils.isValidTimeFormat("11:11"));
        assertTrue(StringUtils.isValidTimeFormat("1:11:00"));
        assertFalse(StringUtils.isValidTimeFormat("no time"));
    }

    @org.junit.jupiter.api.Test
    void formatTime() {
        assertEquals("00:01", StringUtils.formatTime(1000L));
    }

    @org.junit.jupiter.api.Test
    void getAvailableTimes() {
    }

    @org.junit.jupiter.api.Test
    void convertMsToHoursAndMinutes() {
    }

    @org.junit.jupiter.api.Test
    void getTrackPosition() {
    }

    @org.junit.jupiter.api.Test
    void shortenString() {
    }

    @org.junit.jupiter.api.Test
    void countLetters() {
    }
}