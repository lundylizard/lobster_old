package me.lundy.lobster;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.lundy.lobster.command.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigManager;
import me.lundy.lobster.database.DatabaseManager;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.listeners.VoiceDisconnectListener;
import me.lundy.lobster.listeners.buttons.DataButtonListener;
import me.lundy.lobster.listeners.buttons.QueueButtonListener;
import me.lundy.lobster.utils.ValueChangeDetector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Lobster {

    public static final String DISCORD_URL = "https://discord.gg/Hk5YP5AWhW";
    private static Lobster instance;
    private static CommandManager commandManager;
    private ConfigManager configManager;
    private DatabaseManager database;

    public static void main(String[] args) {
        instance = new Lobster();
        instance.configManager = ConfigManager.getInstance();
        instance.database = new DatabaseManager();
        commandManager = new CommandManager();
        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createLight(instance.getConfig().getBotToken());
        shardManagerBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardManagerBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardManagerBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);
        shardManagerBuilder.addEventListeners(
                new QueueButtonListener(),
                new VoiceDisconnectListener(),
                new DataButtonListener(),
                commandManager
        );
        ShardManager shardManager = shardManagerBuilder.build();
        JDA rootShard = shardManager.getShards().get(0);
        rootShard.setRequiredScopes("applications.commands");
        rootShard.updateCommands().addCommands(commandManager.getCommandDataList()).queue(Lobster::setCommandIds);

        ValueChangeDetector<Integer> guildAmount = new ValueChangeDetector<>(() -> shardManager.getGuilds().size());
        guildAmount.registerAndAccept(0, (serverCount) -> {
            Activity activity = Activity.customStatus(String.format("on %d servers", serverCount));
            shardManager.setActivity(activity);
        }, 3000); // 3 seconds

        ValueChangeDetector<List<VoiceChannel>> inactivityHandler = new ValueChangeDetector<>(() ->
                shardManager.getGuilds().stream().filter(guild -> guild.getAudioManager().isConnected())
                        .map(guild -> guild.getAudioManager().getConnectedChannel().asVoiceChannel())
                        .filter(audioChannel -> audioChannel.getMembers().stream()
                                .filter(member -> member.getVoiceState().inAudioChannel())
                                .allMatch(member -> member.getVoiceState().isDeafened() || member.getUser().isBot()))
                        .toList());

        Consumer<List<VoiceChannel>> inactivityAction = voiceChannels -> voiceChannels.forEach(voiceChannel -> {
            Guild guild = voiceChannel.getGuild();
            AudioManager audioManager = guild.getAudioManager();
            audioManager.closeAudioConnection();
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
            AudioPlayer audioPlayer = musicManager.audioPlayer;
            audioPlayer.stopTrack();
            musicManager.scheduler.queue.clear();
        });

        inactivityHandler.register(Collections.emptyList(), inactivityAction, 60000); // 1 minute
    }

    private static void setCommandIds(List<Command> commands) {
        commands.forEach(command -> commandManager.getCommands().get(command.getName()).setId(command.getIdLong()));
    }

    public BotConfig getConfig() {
        return instance.configManager.getBotConfig();
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static Lobster getInstance() {
        return instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

}