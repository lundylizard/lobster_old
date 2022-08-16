package de.lundy.lobster.lavaplayer;

import com.github.topislavalinkplugins.topissourcemanagers.applemusic.AppleMusicSourceManager;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifyConfig;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.lundy.lobster.Lobster;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {

        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        // Set spotify config values
        SpotifyConfig spotifyConfig = new SpotifyConfig();
        spotifyConfig.setClientId(Lobster.config.getSpotifyClientId());
        spotifyConfig.setClientSecret(Lobster.config.getSpotifyClientSecret());
        spotifyConfig.setCountryCode("US");

        // Register external source managers
        audioPlayerManager.registerSourceManager(new SpotifySourceManager(null, spotifyConfig, this.audioPlayerManager));
        audioPlayerManager.registerSourceManager(new AppleMusicSourceManager(null, "US", this.audioPlayerManager));

        // LavaPlayer default sources
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

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

    public void loadAndPlay(InteractionHook interactionHook, String trackUrl, boolean top) {

        GuildMusicManager musicManager = this.getMusicManager(interactionHook.getInteraction().getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queueSong(track, top);
                interactionHook.editOriginal(String.format("Added `%s` by `%s`", track.getInfo().title, track.getInfo().author)).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                if (audioPlaylist.isSearchResult()) {
                    AudioTrack track = audioPlaylist.getTracks().get(0);
                    musicManager.scheduler.queueSong(track, top);
                    interactionHook.editOriginal(String.format("Added to the queue: `%s` by `%s`", track.getInfo().title, track.getInfo().author)).queue();
                    return;
                }

                List<AudioTrack> trackList = audioPlaylist.getTracks();

                for (AudioTrack track : trackList) {
                    musicManager.scheduler.queueSong(track, top);
                }

                interactionHook.editOriginal(String.format("Added `%d` songs to the queue.", trackList.size())).queue();

            }


            @Override
            public void noMatches() {
                interactionHook.editOriginal(":warning: Could not find specified song.").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                interactionHook.editOriginal(":warning: Could not load specified song: " + e.getMessage()).queue();
            }

        });

    }

}
