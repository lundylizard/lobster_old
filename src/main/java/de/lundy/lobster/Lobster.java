package de.lundy.lobster;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.lundy.lobster.commands.misc.HelpCommand;
import de.lundy.lobster.commands.misc.InviteCommand;
import de.lundy.lobster.commands.music.*;
import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.listeners.ReadyListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
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
        shardBuilder.addEventListeners(new RadioCommand());

        ShardManager shardManager;

        try {
            shardManager = shardBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        Collection<SlashCommandData> commands = new ArrayList<>();

        // Commands without options
        commands.add(Commands.slash("help", "List of commands you can use."));
        commands.add(Commands.slash("invite", "Invite lobster to your server."));
        commands.add(Commands.slash("join", "Let lobster join a voice channel."));
        commands.add(Commands.slash("leave", "Let lobster leave the voice channel."));
        commands.add(Commands.slash("loop", "Change whether the current song is looping or not."));
        commands.add(Commands.slash("np", "See what song is playing right now."));
        commands.add(Commands.slash("pause", "Toggle pause"));
        commands.add(Commands.slash("queue", "Display the upcoming songs."));
        commands.add(Commands.slash("shuffle", "Shuffle the queue"));
        commands.add(Commands.slash("skip", "Skip the current song"));
        commands.add(Commands.slash("stop", "Stop the music and clear the queue"));

        // Move Command
        SlashCommandData moveCommand = Commands.slash("move", "Move songs in the queue.");
        moveCommand.addOption(OptionType.INTEGER, "from", "What song should be moved.", true);
        moveCommand.addOption(OptionType.INTEGER, "to", "Where the song should be moved to.", true);
        commands.add(moveCommand);

        // Play Command
        SlashCommandData playCommand = Commands.slash("play", "Add a song to the song queue.");
        playCommand.addOption(OptionType.STRING, "search", "Search term or url of the song.");
        playCommand.addOption(OptionType.ATTACHMENT, "attachment", "Add a file to the queue.");
        playCommand.addOption(OptionType.BOOLEAN, "top", "Add this song to the top of the queue.");
        commands.add(playCommand);

        // Remove Command
        SlashCommandData removeCommand = Commands.slash("remove", "Remove a song from the queue.");
        removeCommand.addOption(OptionType.INTEGER, "index", "What song should be removed", true);
        commands.add(removeCommand);

        // Seek Command
        SlashCommandData seekCommand = Commands.slash("seek", "Change the position of the current song");
        seekCommand.addOption(OptionType.INTEGER, "amount", "Amount to seek (seconds)", true);
        commands.add(seekCommand);

        // Volume Command
        SlashCommandData volumeCommand = Commands.slash("volume", "Change the volume");
        volumeCommand.addOption(OptionType.INTEGER, "amount", "Amount of volume");
        commands.add(volumeCommand);

        // Radio Command
        SlashCommandData radioCommand = Commands.slash("radio", "Play a radio station from radio.garden");
        radioCommand.addOption(OptionType.STRING, "location", "Location of the radio station", true, true);
        commands.add(radioCommand);

        // Make every command guild only
        commands.forEach(c -> c.setGuildOnly(true));

        shardManager.getShards().forEach(shard -> shard.updateCommands().addCommands(commands).queue());

        tick(shardManager); // This gets executed every 90 seconds

    }

    private static void tick(ShardManager shardManager) {

        scheduler.scheduleWithFixedDelay(() -> {

            int serverCount = shardManager.getGuilds().size();

            // Update activity to show how many servers this bot is on
            shardManager.getShards().forEach(shard -> shard.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("on " + serverCount + " servers.")));

            for (Guild guilds : shardManager.getGuilds()) {

                // Checks if lobster is connected to a vc
                if (guilds.getAudioManager().isConnected()) {

                    GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guilds);
                    AudioPlayer audioPlayer = musicManager.audioPlayer;

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
