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
import me.lundy.lobster.config.ConfigValues;
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
        String spotifyClientId = Lobster.getInstance().getConfig().getProperty(ConfigValues.SPOTIFY_CLIENT_ID);
        String spotifyClientSecret = Lobster.getInstance().getConfig().getProperty(ConfigValues.SPOTIFY_CLIENT_SECRET);
        audioPlayerManager.registerSourceManager(new SpotifySourceManager(null, spotifyClientId, spotifyClientSecret, "US", this.audioPlayerManager));
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

    public void loadAndPlay(CommandContext context, String trackUrl, boolean top) {
        GuildMusicManager musicManager = this.getMusicManager(context.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, new AudioReference(trackUrl, ""), new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                // Not needed because search is an AudioPlaylist which takes the first song as result
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                if (audioPlaylist.isSearchResult()) {
                    AudioTrack track = audioPlaylist.getTracks().get(0);
                    track.setUserData(context.getExecutor().getAsMention());
                    musicManager.scheduler.queueSong(track, top);
                    context.getEvent().replyFormat("Added `%s` by `%s`", track.getInfo().title, track.getInfo().author).queue();
                    return;
                }

                List<AudioTrack> trackList = audioPlaylist.getTracks();

                for (AudioTrack track : trackList) {
                    track.setUserData(context.getExecutor().getAsMention());
                    musicManager.scheduler.queueSong(track, top);
                }

                context.getEvent().replyFormat("Added `%d` songs to the queue.", trackList.size()).queue();
            }

            @Override
            public void noMatches() {
                context.getEvent().reply(":warning: Could not find specified song").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                context.getEvent().reply(":warning: Could not load specified song: " + e.getMessage()).queue();
            }
        });
    }
}
