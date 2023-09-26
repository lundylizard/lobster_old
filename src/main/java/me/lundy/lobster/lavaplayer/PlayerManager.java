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
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.Guild;

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
        BotConfig config = Lobster.getInstance().getConfig();
        SpotifySourceManager spotifySourceManager = new SpotifySourceManager(
                null,
                config.getSpotifyConfig().getClientId(),
                config.getSpotifyConfig().getClientSecret(),
                config.getSpotifyConfig().getCountryCode(),
                this.audioPlayerManager
        );
        this.audioPlayerManager.registerSourceManager(spotifySourceManager);
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

    public void loadAndPlay(CommandContext context, String query, boolean top) {

        GuildMusicManager musicManager = this.getMusicManager(context.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, new AudioReference(query, ""), new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                AudioTrackUserData audioTrackUserData = new AudioTrackUserData(track.getInfo().title, context.getExecutor().getAsMention());
                track.setUserData(audioTrackUserData);
                musicManager.scheduler.queueSong(track, top);
                context.getEvent().replyFormat(Reply.PLAYER_TRACK_ADDED.getMessage(), track.getInfo().title, track.getInfo().author).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                if (audioPlaylist.isSearchResult()) {
                    AudioTrack track = audioPlaylist.getTracks().get(0);
                    AudioTrackUserData audioTrackUserData = new AudioTrackUserData(query.replace("ytsearch:", ""), context.getExecutor().getAsMention());
                    track.setUserData(audioTrackUserData);
                    musicManager.scheduler.queueSong(track, top);
                    context.getEvent().replyFormat(Reply.PLAYER_TRACK_ADDED.getMessage(), track.getInfo().title, track.getInfo().author).queue();
                    return;
                }

                List<AudioTrack> trackList = audioPlaylist.getTracks();

                for (AudioTrack track : trackList) {
                    AudioTrackUserData audioTrackUserData = new AudioTrackUserData(track.getInfo().title, context.getExecutor().getAsMention());
                    track.setUserData(audioTrackUserData);
                    musicManager.scheduler.queueSong(track, top);
                }

                context.getEvent().replyFormat(Reply.PLAYER_PLAYLIST_ADDED.getMessage(), audioPlaylist.getName(), audioPlaylist.getTracks().size()).queue();
            }

            @Override
            public void noMatches() {
                context.getEvent().reply(Reply.PLAYER_TRACK_NOT_FOUND.getMessage()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                context.getEvent().replyFormat(Reply.PLAYER_LOAD_FAILED.getMessage(), e.getMessage()).queue();
            }
        });
    }

}
