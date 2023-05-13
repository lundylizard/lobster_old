package me.lundy.lobster;

import me.lundy.lobster.command.console.ConsoleCommandManager;
import me.lundy.lobster.command.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigValues;
import me.lundy.lobster.listeners.*;
import me.lundy.lobster.utils.BotUtils;
import me.lundy.lobster.utils.TextChannelCollector;
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
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobster {

    private static final Activity SERVER_COUNT_ACTIVITY = Activity.playing("on %d servers.");
    private static final int SHARD_COUNT = 5;

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
    private Scanner scanner;
    private ConsoleCommandManager consoleCommandManager;
    private TextChannelCollector channelCollector;

    // TODOS:
    // -
    // - First and last page buttons for queue
    // - Display current song in queue list (including who requested it?)
    // - Clear queue and stop playing when forced to disconnect

    public static void main(String[] args) {

        instance = new Lobster();
        instance.scanner = new Scanner(System.in);

        try {
            instance.config = BotConfig.getInstance();
        } catch (IOException e) {
            instance.logger.error("Could not load configuration", e);
            return;
        }

        instance.commandManager = new CommandManager();
        instance.consoleCommandManager = new ConsoleCommandManager();
        instance.channelCollector = new TextChannelCollector();

        DefaultShardManagerBuilder shardBuilder = DefaultShardManagerBuilder.createLight(instance.config.getProperty(ConfigValues.BOT_TOKEN));

        shardBuilder.setShardsTotal(SHARD_COUNT);
        shardBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);

        shardBuilder.addEventListeners(new GuildJoinListener());
        shardBuilder.addEventListeners(new GuildLeaveListener());
        shardBuilder.addEventListeners(new QueueButtonListener());
        shardBuilder.addEventListeners(new ReadyListener());
        shardBuilder.addEventListeners(new VcLeaveListener());
        shardBuilder.addEventListeners(instance.commandManager);

        ShardManager shardManager = shardBuilder.build();

        List<SlashCommandData> commands = new ArrayList<>(instance.commandManager.getCommandDataList());
        shardManager.getShards().forEach(shard -> shard.updateCommands().addCommands(commands).complete());

        instance.scheduler.scheduleWithFixedDelay(() -> {
            Lobster.instance.tick(shardManager);
        }, 0, 1, TimeUnit.MINUTES);

        instance.handleConsoleInput(shardManager);

    }

    private void handleConsoleInput(ShardManager shardManager) {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            consoleCommandManager.handleCommand(input, shardManager);
        }
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

    public BotConfig getConfig() {
        return config;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConsoleCommandManager getConsoleCommandManager() {
        return consoleCommandManager;
    }

    public TextChannelCollector getChannelCollector() {
        return channelCollector;
    }

    public static Lobster getInstance() {
        return Lobster.instance;
    }

}
