package me.lundy.lobster.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.lundy.lobster.utils.Pagination;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer player;
    public final BlockingDeque<AudioTrack> queue;
    private boolean repeating = false;
    private Pagination<AudioTrack> pagination;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingDeque<>();
    }

    public Pagination<AudioTrack> getPagination() {
        if (this.pagination == null) {
            this.pagination = new Pagination<>(new ArrayList<>(this.queue));
        }
        return this.pagination;
    }

    public void queueSong(AudioTrack track, boolean top) {
        if (!this.player.startTrack(track, true)) {
            if (top) {
                this.queue.addFirst(track);
                return;
            }
            this.queue.offerLast(track);
        }
    }

    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        if (endReason.mayStartNext) {
            if (repeating) {
                this.player.startTrack(track.makeClone(), false);
                return;
            }
            nextTrack();
        }

    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

}
