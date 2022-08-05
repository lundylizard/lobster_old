package de.lundy.lobster;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.lundy.lobster.commands.misc.HelpCommand;
import de.lundy.lobster.commands.misc.InviteCommand;
import de.lundy.lobster.commands.music.*;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.listeners.ReadyListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobster {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static LobsterConfig config;

    public static void main(String @NotNull [] args) {

        Gson gson = new Gson();
        JsonReader reader;

        try {
            reader = new JsonReader(new FileReader("config.json"));
        } catch (FileNotFoundException e) {
            System.err.printf("Could not find config.json: %s%n", e.getMessage());
            return;
        }

        config = gson.fromJson(reader, LobsterConfig.class);

        DefaultShardManagerBuilder shardBuilder = DefaultShardManagerBuilder.createLight(config.getToken());
        shardBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);
        shardBuilder.addEventListeners(new ReadyListener());
        shardBuilder.addEventListeners(new HelpCommand());
        shardBuilder.addEventListeners(new InviteCommand());
        shardBuilder.addEventListeners(new JoinCommand());
        shardBuilder.addEventListeners(new LeaveCommand());
        shardBuilder.addEventListeners(new LoopCommand());
        shardBuilder.addEventListeners(new MoveCommand());
        shardBuilder.addEventListeners(new NowPlayingCommand());
        shardBuilder.addEventListeners(new PauseToggleCommand());
        shardBuilder.addEventListeners(new PlayCommand());
        shardBuilder.addEventListeners(new QueueCommand());
        shardBuilder.addEventListeners(new RemoveCommand());
        shardBuilder.addEventListeners(new SeekCommand());
        shardBuilder.addEventListeners(new ShuffleCommand());
        shardBuilder.addEventListeners(new SkipCommand());
        shardBuilder.addEventListeners(new StopCommand());
        shardBuilder.addEventListeners(new VolumeCommand());

        ShardManager shardManager;

        try {
            shardManager = shardBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        shardManager.getShards().forEach(shard -> shard.updateCommands().addCommands(

            Commands.slash("help", "List of commands you can use.").setGuildOnly(true), Commands.slash("invite", "Invite lobster to your server.").setGuildOnly(true), Commands.slash("join", "Let lobster join a voice channel.").setGuildOnly(true), Commands.slash("leave", "Let lobster leave the voice channel.").setGuildOnly(true), Commands.slash("loop", "Change whether the current song is looping or not.").setGuildOnly(true), Commands.slash("move", "Move songs in the queue.").setGuildOnly(true).addOption(OptionType.INTEGER, "from", "What song should be moved.", true).addOption(OptionType.INTEGER, "to", "Where the song should be moved to.", true), Commands.slash("np", "See what song is playing right now.").setGuildOnly(true), Commands.slash("pause", "Toggle pause").setGuildOnly(true), Commands.slash("play", "Add a song to the song queue.").addOption(OptionType.BOOLEAN, "top", "Add this song to the top of the queue.").addOption(OptionType.STRING, "search", "Search term or url of the song.").addOption(OptionType.ATTACHMENT, "attachment", "Add a file to the queue.").setGuildOnly(true), Commands.slash("queue", "Display the upcoming songs.").setGuildOnly(true), Commands.slash("remove", "Remove a song from the queue.").addOption(OptionType.INTEGER, "index", "What song should be removed", true).setGuildOnly(true), Commands.slash("seek", "Change the position of the current song").addOption(OptionType.INTEGER, "amount", "Amount to seek (seconds)", true).setGuildOnly(true), Commands.slash("shuffle", "Shuffle the queue").setGuildOnly(true), Commands.slash("skip", "Skip the current song").setGuildOnly(true), Commands.slash("stop", "Stop the music and clear the queue").setGuildOnly(true), Commands.slash("volume", "Change the volume").addOption(OptionType.INTEGER, "amount", "Amount of volume").setGuildOnly(true)

        ).queue());

        tick(shardManager); // This gets executed every 90 seconds

    }

    private static void tick(ShardManager shardManager) {

        scheduler.scheduleWithFixedDelay(() -> {

            var serverCount = shardManager.getGuilds().size();

            // Update activity to show how many servers this bot is on
            shardManager.getShards().forEach(shard -> shard.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("on " + serverCount + " servers.")));

            for (var guilds : shardManager.getGuilds()) {

                // Checks if lobster is connected to a vc
                if (guilds.getAudioManager().isConnected()) {

                    var musicManager = PlayerManager.getInstance().getMusicManager(guilds);
                    var audioPlayer = musicManager.audioPlayer;

                    // If there is no other un-deafened member or bot in vc stop playing music and leave vc
                    if (guilds.getAudioManager().getConnectedChannel().getMembers().stream().noneMatch(x -> !x.getVoiceState().isDeafened() && !x.getUser().isBot())) {

                        guilds.getAudioManager().closeAudioConnection();
                        audioPlayer.stopTrack();
                        musicManager.scheduler.queue.clear();

                    }
                }
            }

        }, 0, 90, TimeUnit.SECONDS);

    }

    public static class LobsterConfig {

        private final String token;
        private final String spotifyClientId;
        private final String spotifyClientSecret;

        public LobsterConfig(String token, String spotifyClientId, String spotifyClientSecret) {
            this.token = token;
            this.spotifyClientId = spotifyClientId;
            this.spotifyClientSecret = spotifyClientSecret;
        }

        public String getToken() {
            return token;
        }

        public String getSpotifyClientId() {
            return spotifyClientId;
        }

        public String getSpotifyClientSecret() {
            return spotifyClientSecret;
        }

    }

}
