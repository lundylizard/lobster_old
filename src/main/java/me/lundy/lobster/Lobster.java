package me.lundy.lobster;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.lundy.lobster.command.CommandManager;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.listeners.QueueButtonListener;
import me.lundy.lobster.listeners.VoiceDisconnectListener;
import me.lundy.lobster.utils.ValueChangeDetector;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Lobster {

    public static final String DISCORD_URL = "https://discord.gg/Hk5YP5AWhW";
    private static CommandManager commandManager;
    private static final Logger logger = LoggerFactory.getLogger(Lobster.class);

    public static void main(String[] args) {

        commandManager = new CommandManager();
        var token = System.getenv("DISCORD_TOKEN");
        var shardManagerBuilder = DefaultShardManagerBuilder.createLight(token);
        logger.info("Using token {}", token);
        shardManagerBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardManagerBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardManagerBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);
        shardManagerBuilder.addEventListeners(new QueueButtonListener(), new VoiceDisconnectListener(), commandManager);
        var shardManager = shardManagerBuilder.build();

        var guildAmount = new ValueChangeDetector<>(() -> shardManager.getGuilds().size());
        guildAmount.register(0, (serverCount) -> {
            Activity activity = Activity.customStatus(String.format("on %d servers", serverCount));
            shardManager.setActivity(activity);
        }, 3_000); // 3 seconds

        var inactivityHandler = new ValueChangeDetector<>(() ->
                shardManager.getGuilds().stream().filter(guild -> guild.getAudioManager().isConnected())
                        .map(guild -> guild.getAudioManager().getConnectedChannel())
                        .filter(Objects::nonNull).map(AudioChannelUnion::asVoiceChannel)
                        .filter(audioChannel -> audioChannel.getMembers().stream()
                                .map(Member::getVoiceState).filter(Objects::nonNull)
                                .filter(GuildVoiceState::inAudioChannel)
                                .allMatch(voiceState -> voiceState.isDeafened() || voiceState.getMember().getUser().isBot()))
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

        inactivityHandler.register(Collections.emptyList(), inactivityAction, 60_000); // 1 minute
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

}