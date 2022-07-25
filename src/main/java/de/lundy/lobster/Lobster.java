package de.lundy.lobster;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.lundy.lobster.commands.impl.CommandHandler;
import de.lundy.lobster.commands.misc.HelpCommand;
import de.lundy.lobster.commands.misc.InviteCommand;
import de.lundy.lobster.commands.misc.PrefixCommand;
import de.lundy.lobster.commands.music.*;
import de.lundy.lobster.database.LobsterDatabase;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.listeners.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobster {

    private static LobsterDatabase database;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String @NotNull [] args) {

        Gson gson = new Gson();
        JsonReader reader;

        try {
            reader = new JsonReader(new FileReader("config.json"));
        } catch (FileNotFoundException e) {
            System.err.printf("Could not find config.json: %s%n", e.getMessage());
            return;
        }

        LobsterConfig config = gson.fromJson(reader, LobsterConfig.class);
        database = new LobsterDatabase(config.getPassword());

        database.createTables();

        var shardBuilder = DefaultShardManagerBuilder.createLight(args[0])

            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS)

            .disableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS).setLargeThreshold(50)

            .addEventListeners(new MessageCommandListener()).addEventListeners(new JoinListener()).addEventListeners(new VCLeaveListener()).addEventListeners(new VCJoinListener()).addEventListeners(new ReadyListener());

        // Register commands
        CommandHandler.addCommand(new LeaveCommand(), "dc", "disconnect", "leave");
        CommandHandler.addCommand(new QueueCommand(), "queue", "q");
        CommandHandler.addCommand(new PrefixCommand(), "prefix");
        CommandHandler.addCommand(new InviteCommand(), "invite");
        CommandHandler.addCommand(new NowPlayingCommand(), "np");
        CommandHandler.addCommand(new HelpCommand(), "help");
        CommandHandler.addCommand(new ResumeCommand(), "resume", "unpause");
        CommandHandler.addCommand(new MoveCommand(), "move", "mv", "m");
        CommandHandler.addCommand(new PlayCommand(), "play", "p", "sr");
        CommandHandler.addCommand(new RemoveCommand(), "remove", "rm");
        CommandHandler.addCommand(new ShuffleCommand(), "shuffle");
        CommandHandler.addCommand(new SkipCommand(), "skip", "s");
        CommandHandler.addCommand(new PauseCommand(), "pause");
        CommandHandler.addCommand(new JoinCommand(), "join");
        CommandHandler.addCommand(new SeekCommand(), "seek");
        CommandHandler.addCommand(new LoopCommand(), "loop");
        CommandHandler.addCommand(new LinkCommand(), "link");
        CommandHandler.addCommand(new StopCommand(), "stop");

        ShardManager shard = null;

        try {
            shard = shardBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        tick(shard); // This gets executed every 60 seconds

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
                    if (Objects.requireNonNull(guilds.getAudioManager().getConnectedChannel()).getMembers().stream().noneMatch(x -> ! Objects.requireNonNull(x.getVoiceState()).isDeafened() && ! x.getUser().isBot())) {

                        guilds.getAudioManager().closeAudioConnection();
                        audioPlayer.stopTrack();
                        musicManager.scheduler.queue.clear();

                    }
                }
            }

        }, 0, 60, TimeUnit.SECONDS);

    }

    public static LobsterDatabase getDatabase() {
        return database;
    }

    public static class LobsterConfig {

        private final String token;
        private final String password;
        private final String spotifyClientId;
        private final String spotifyClientSecret;
        private final String youtubePsid;
        private final String youtubePapisid;

        public LobsterConfig(String token, String password, String spotifyClientId, String spotifyClientSecret, String youtubePsid, String youtubePapisid) {
            this.token = token;
            this.password = password;
            this.spotifyClientId = spotifyClientId;
            this.spotifyClientSecret = spotifyClientSecret;
            this.youtubePsid = youtubePsid;
            this.youtubePapisid = youtubePapisid;
        }

        public String getToken() {
            return token;
        }

        public String getPassword() {
            return password;
        }

        public String getSpotifyClientId() {
            return spotifyClientId;
        }

        public String getSpotifyClientSecret() {
            return spotifyClientSecret;
        }

        public String getYoutubePsid() {
            return youtubePsid;
        }

        public String getYoutubePapisid() {
            return youtubePapisid;
        }
    }

}
