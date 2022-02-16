package de.lundy.lobster.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.lundy.lobster.lavaplayer.spotify.SpotifyToYoutubeInterpreter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
        new SpotifyToYoutubeInterpreter();
    }

    public GuildMusicManager getMusicManager(@NotNull Guild guild) {

        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            var guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });

    }

    public void loadAndPlay(@NotNull MessageReceivedEvent event, String trackUrl, boolean top) {

        var musicManager = this.getMusicManager(event.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                musicManager.scheduler.queue(audioTrack, top);
                event.getChannel().sendMessage(":arrow_forward: Added to the queue: " + audioTrack.getInfo().title + ", by " + audioTrack.getInfo().author).queue();

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                if (audioPlaylist.isSearchResult()) {

                    var track = audioPlaylist.getTracks().get(0);
                    event.getChannel().sendMessage(":arrow_forward: Added to the queue: " + track.getInfo().title + ", by " + track.getInfo().author).queue();
                    musicManager.scheduler.queue(track, top);

                } else {

                    var trackList = audioPlaylist.getTracks();
                    event.getChannel().sendMessage(":arrow_forward: Added " + trackList.size() + " songs to the queue.").queue();

                    for (var track : trackList) {
                        musicManager.scheduler.queue(track, top);
                    }

                }
            }

            @Override
            public void noMatches() {
                event.getChannel().sendMessage(":warning: Could not find specified song.").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.getChannel().sendMessage(":warning: Could not load specified song.").queue();
            }
        });

    }

    public static PlayerManager getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;

    }
}
