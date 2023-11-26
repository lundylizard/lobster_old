package me.lundy.lobster.utils;

public enum Reply {

    EXECUTOR_NOT_IN_VOICE("`⚠️` You are not in a voice channel"),
    SELF_ALREADY_IN_VOICE("`⚠️` I am already in a voice channel"),
    SELF_NOT_IN_VOICE("`⚠️` I am not in a voice channel"),
    NOT_IN_SAME_VOICE("`⚠️` We are not in the same voice channel"),
    ERROR_VOICE_CHANNEL_NULL("`⚠️` Unexpected error: Could not find voice channel"),
    ERROR_NO_PERMISSIONS_VOICE("`⚠️` I do not have enough permissions to join that channel"),
    JOINED_VOICE("`🔊` Joined voice channel **%s**"),
    LEFT_VOICE("`❌` Left the voice channel"),
    LOOP_STATE("%s looping current song"),
    NO_TRACK_PLAYING("`⚠️` There is currently no track playing"),
    COULD_NOT_FIND_LYRICS("`⚠️` Could not find lyrics for song. Search query: `%s`"),
    ERROR_FETCHING_LYRICS("`⚠️` An error occurred while fetching the lyrics: %s"),
    LYRICS_TOO_LONG("`⚠️` Lyrics are too long to be displayed: %s"),
    MOVE_POSITION_INVALID("`⚠️` The new position has to be higher than **0**"),
    MOVE_SUCCESSFUL("Successfully moved track **%s** to `#%d`"),
    PAUSED("%s current song"),
    QUEUE_EMPTY("`⚠️` The queue is currently empty"),
    TRACK_NOT_IN_QUEUE("`⚠️` This song is not in the queue"),
    REMOVED_SONG("Removed song **%s - %s** from the queue."),
    INVALID_TIME_FORMAT("`⚠️` Invalid time format: Please use `hh:mm:ss` or `mm:ss`"),
    TRACK_SKIPPED("`⏭️` Skipped **%s - %s**"),
    TRACK_SET_POSITION("Set song position to **%s**"),
    QUEUE_SHUFFLED("Successfully shuffled the queue"),
    SKIP_NEXT_SONG("\n`🎵` Now Playing: **%s - %s**"),
    STOPPED_PLAYBACK("`❌` Stopped the playback"),
    CURRENT_VOLUME("`🔊` Current volume is **%d%%**"),
    VOLUME_LOWER("`❌` Volume cannot be lower than **%d%%**"),
    VOLUME_HIGHER("`❌` Volume cannot be higher than **%d%%**"),
    VOLUME_CHANGED("`🔊` Changed volume to **%d%%**"),
    PLAYER_TRACK_ADDED("`▶️` Added **%s** by **%s**"),
    PLAYER_PLAYLIST_ADDED("`▶️` Added playlist **%s** (%d songs) to the queue"),
    PLAYER_TRACK_NOT_FOUND("`⚠️` Could not find specified song"),
    PLAYER_LOAD_FAILED("`⚠️` Could not load specified song: %s"),
    QUEUE_OUTDATED("`⚠️` The queue list is outdated");

    private final String message;

    Reply(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
