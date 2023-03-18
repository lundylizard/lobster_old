package me.lundy.lobster;

import me.lundy.lobster.commands.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigValues;
import me.lundy.lobster.listeners.GuildJoinListener;
import me.lundy.lobster.listeners.GuildLeaveListener;
import me.lundy.lobster.listeners.QueueButtonListener;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobster {

    private static final Activity SERVER_COUNT_ACTIVITY = Activity.playing("on %d servers.");
    private static final int SHARD_COUNT = 4;

    public static final String INVITE_URL = "https://discord.com/api/oauth2/authorize?" +
            "client_id=891760327522394183" +
            "&permissions=2150647808" +
            "&scope=bot%20applications.commands";
    public static final String DISCORD_URL = "https://discord.gg/Hk5YP5AWhW";
    public static boolean debug;

    private static Lobster instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(Lobster.class);
    private BotConfig config;
    private CommandManager commandManager;

    public static void main(String[] args) {

        instance = new Lobster();

        try {
            instance.config = BotConfig.getInstance();
        } catch (IOException e) {
            instance.logger.error("Could not load configuration", e);
            return;
        }

        debug = false; //Boolean.parseBoolean(instance.config.getProperty(ConfigValues.DEBUG_MODE));

        Lobster.instance.commandManager = new CommandManager();

        DefaultShardManagerBuilder shardBuilder = DefaultShardManagerBuilder.createLight(Lobster.instance.config.getProperty(ConfigValues.BOT_TOKEN));

        // shardBuilder.setShardsTotal(SHARD_COUNT);
        shardBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        shardBuilder.addEventListeners(new GuildJoinListener());
        shardBuilder.addEventListeners(new GuildLeaveListener());
        shardBuilder.addEventListeners(new QueueButtonListener());
        shardBuilder.addEventListeners(Lobster.instance.commandManager);

        ShardManager shardManager = shardBuilder.build();

        List<SlashCommandData> commands = new ArrayList<>(Lobster.instance.commandManager.getCommandDataList());
        shardManager.getShards().forEach(shard -> shard.updateCommands().addCommands(commands).complete());

        Lobster.instance.scheduler.scheduleWithFixedDelay(() -> {
            Lobster.instance.tick(shardManager);
        }, 0, 1, TimeUnit.MINUTES);

    }

    public BotConfig getConfig() {
        return config;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static Lobster getInstance() {
        return Lobster.instance;
    }

    private void tick(ShardManager shardManager) {
        updatePresence(shardManager);

        List<CompletableFuture<Void>> futures = shardManager.getGuilds().stream()
                .map(guild -> CompletableFuture.runAsync(() -> BotUtils.handleInactivity(guild)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    }

    private void updatePresence(ShardManager shardManager) {
        int serverCount = shardManager.getGuilds().size();
        Activity updatedActivity = Activity.playing(String.format(SERVER_COUNT_ACTIVITY.getName(), serverCount));
        shardManager.getShards().forEach(shard -> {
            shard.getPresence().setPresence(OnlineStatus.ONLINE, updatedActivity);
        });
    }

    public Logger getLogger() {
        return logger;
    }
}
