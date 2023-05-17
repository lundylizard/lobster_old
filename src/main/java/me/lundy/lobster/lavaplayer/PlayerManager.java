package me.lundy.lobster.lavaplayer;

import com.github.topisenpai.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.config.ConfigValues;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionHook;

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

        // Register external source managers
        String spotifyClientId = Lobster.getInstance().getConfig().getProperty(ConfigValues.SPOTIFY_CLIENT_ID);
        String spotifyClientSecret = Lobster.getInstance().getConfig().getProperty(ConfigValues.SPOTIFY_CLIENT_SECRET);
        audioPlayerManager.registerSourceManager(new SpotifySourceManager(null, spotifyClientId, spotifyClientSecret, "US", this.audioPlayerManager));

        // LavaPlayer default sources
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(InteractionHook interactionHook, String trackUrl, boolean top) {

        if (interactionHook == null) return;
        if (interactionHook.getInteraction().getGuild() == null) return;

        GuildMusicManager musicManager = this.getMusicManager(interactionHook.getInteraction().getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, new AudioReference(trackUrl, ""), new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                track.setUserData(interactionHook.getInteraction().getMember().getAsMention());
                musicManager.scheduler.queueSong(track, top);
                interactionHook.editOriginal(String.format("Added `%s` by `%s`", track.getInfo().title, track.getInfo().author)).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                if (audioPlaylist.isSearchResult()) {
                    AudioTrack track = audioPlaylist.getTracks().get(0);
                    track.setUserData(interactionHook.getInteraction().getMember().getAsMention());
                    musicManager.scheduler.queueSong(track, top);
                    interactionHook.editOriginal(String.format("Added to the queue: `%s` by `%s`", track.getInfo().title, track.getInfo().author)).queue();
                    return;
                }

                List<AudioTrack> trackList = audioPlaylist.getTracks();

                for (AudioTrack track : trackList) {
                    if (musicManager.scheduler.queue.contains(track)) {
                        return;
                    }
                    track.setUserData(interactionHook.getInteraction().getMember().getAsMention());
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
