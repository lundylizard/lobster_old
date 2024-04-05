package me.lundy.lobster.lavaplayer;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new ConcurrentHashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        SpotifySourceManager spotifySourceManager = new SpotifySourceManager(
                null,
                "1a0e7d17c77b46749c1d3ec65fdf574a",
                "9a9a4c60219b4792b166edbfd40d1045",
                "US",
                this.audioPlayerManager
        );
        this.audioPlayerManager.registerSourceManager(spotifySourceManager);
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public void loadAndPlay(SlashCommandInteractionEvent event, String query, boolean top) {

        GuildMusicManager musicManager = this.getMusicManager(event.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, new AudioReference(query, ""), new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {

                setUserData(track, query, event);
                String trackTitle = track.getInfo().title;
                String trackAuthor = track.getInfo().author;

                if (track.getSourceManager().getSourceName().equals("http")) {
                    try {
                        URL url = URI.create(track.getInfo().uri).toURL();
                        if (trackTitle.isEmpty()) trackTitle = Paths.get(url.getPath()).getFileName().toString();
                        if (trackAuthor.isEmpty()) trackAuthor = "File";
                    } catch (MalformedURLException e) {
                        throw new IllegalStateException("There was an error parsing the URL of an HTTP Source", e);
                    }
                }

                musicManager.scheduler.queueSong(track, top);
                event.getHook().editOriginalFormat(Reply.PLAYER_TRACK_ADDED.getMessage(), trackTitle, trackAuthor).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                if (audioPlaylist.isSearchResult()) {
                    var track = audioPlaylist.getTracks().get(0);
                    trackLoaded(track);
                    return;
                }

                List<AudioTrack> trackList = audioPlaylist.getTracks();

                for (AudioTrack track : trackList) {
                    setUserData(track, query, event);
                    musicManager.scheduler.queueSong(track, top);
                }

                event.getHook().editOriginalFormat(Reply.PLAYER_PLAYLIST_ADDED.getMessage(), audioPlaylist.getName(), audioPlaylist.getTracks().size()).queue();
            }

            @Override
            public void noMatches() {
                event.getHook().editOriginalFormat(Reply.PLAYER_TRACK_NOT_FOUND.getMessage()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.getHook().editOriginalFormat(Reply.PLAYER_LOAD_FAILED.getMessage(), e.getMessage()).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (instance == null) instance = new PlayerManager();
        return instance;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            var guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }


    public void setUserData(AudioTrack track, String query, SlashCommandInteractionEvent event) {
        var audioTrackUserData = new AudioTrackUserData(query.replace("ytsearch:", ""), event.getMember().getAsMention());
        track.setUserData(audioTrackUserData);
    }

}
