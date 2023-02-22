package me.lundy.lobster;

import me.lundy.lobster.commands.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigValues;
import me.lundy.lobster.listeners.GuildJoinListener;
import me.lundy.lobster.listeners.GuildLeaveListener;
import me.lundy.lobster.listeners.ReadyListener;
import me.lundy.lobster.utils.InactivityManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobster {

    public static final String INVITE_URL = "https://discord.com/api/oauth2/authorize?" +
            "client_id=891760327522394183" +
            "&permissions=2150647808" +
            "&scope=bot%20applications.commands";
    public static final String DISCORD_URL = "https://discord.gg/Hk5YP5AWhW";

    private static Lobster instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(Lobster.class);
    private BotConfig config;
    private CommandManager commandManager;
    private InactivityManager inactivityManager;

    public static void main(String[] args) {

        Lobster.instance = new Lobster();
        Lobster.instance.config = BotConfig.getInstance();
        Lobster.instance.commandManager = new CommandManager();
        Lobster.instance.inactivityManager = InactivityManager.getInstance();

        DefaultShardManagerBuilder shardBuilder = DefaultShardManagerBuilder.createLight(Lobster.instance.config.getProperty(ConfigValues.BOT_TOKEN));

        shardBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        shardBuilder.addEventListeners(new ReadyListener());
        shardBuilder.addEventListeners(new GuildJoinListener());
        shardBuilder.addEventListeners(new GuildLeaveListener());
        shardBuilder.addEventListeners(Lobster.instance.commandManager);

        ShardManager shardManager = shardBuilder.build();

        List<SlashCommandData> commands = new ArrayList<>(Lobster.instance.commandManager.getCommandDataList());
        shardManager.getShards().forEach(shard -> shard.updateCommands().addCommands(commands).queue());

        Lobster.instance.scheduler.scheduleWithFixedDelay(() -> {
            Lobster.instance.tick(shardManager);
        }, 0, 1, TimeUnit.MINUTES);

    }

    public static BotConfig getConfig() {
        return Lobster.instance.config;
    }

    public static Lobster getInstance() {
        return Lobster.instance;
    }

    private void tick(ShardManager shardManager) {
        updatePresence(shardManager);
        inactivityManager.handleInactivity(shardManager);
    }

    private void updatePresence(ShardManager shardManager) {
        int serverCount = shardManager.getGuilds().size();
        shardManager.setPresence(OnlineStatus.ONLINE, Activity.playing("on " + serverCount + " servers"));
    }

    public InactivityManager getInactivityManager() {
        return inactivityManager;
    }

    public Logger getLogger() {
        return logger;
    }
}
