package me.lundy.lobster;

import me.lundy.lobster.command.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigManager;
import me.lundy.lobster.database.DatabaseManager;
import me.lundy.lobster.handler.InactivityHandler;
import me.lundy.lobster.listeners.VoiceDisconnectListener;
import me.lundy.lobster.listeners.buttons.QueueButtonListener;
import me.lundy.lobster.database.settings.Setting;
import me.lundy.lobster.database.settings.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobster {

    public static final String DISCORD_URL = "https://discord.gg/Hk5YP5AWhW";

    private static Lobster instance;
    private static CommandManager commandManager;
    private ConfigManager configManager;
    private DatabaseManager database;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean debugMode;

    public static void main(String[] args) {
        instance = new Lobster();
        instance.debugMode = Arrays.stream(args).anyMatch("-debug"::contains);
        instance.configManager = ConfigManager.getInstance();
        instance.database = new DatabaseManager();
        commandManager = new CommandManager();
        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createLight(instance.getConfig().getBotToken());
        shardManagerBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardManagerBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardManagerBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);
        shardManagerBuilder.addEventListeners(new QueueButtonListener());
        // FIXME shardManagerBuilder.addEventListeners(new BotActivityManager());
        shardManagerBuilder.addEventListeners(new VoiceDisconnectListener());
        shardManagerBuilder.addEventListeners(commandManager);
        ShardManager shardManager = shardManagerBuilder.build();
        JDA rootShard = shardManager.getShards().get(0);
        rootShard.setRequiredScopes("applications.commands");
        rootShard.updateCommands().addCommands(commandManager.getCommandDataList()).queue(Lobster::setCommandIds);
        instance.scheduler.scheduleWithFixedDelay(() -> instance.tick(shardManager), 0, 1, TimeUnit.MINUTES);
        SettingsManager settingsManager = new SettingsManager(instance.database.getDataSource(), 0L);
        try {
            Setting<?> keepVolume = settingsManager.getSetting("keepVolume");
            if (keepVolume.getType().equals(Boolean.class)) {
                System.out.println("boolean");
            }
            if (keepVolume.getType().equals(String.class)) {
                System.out.println("string");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void tick(ShardManager shardManager) {
        handleInactivity(shardManager);
    }

    private void handleInactivity(ShardManager shardManager) {
        shardManager.getGuilds().forEach(InactivityHandler::handleInactivity);
    }

    private static void setCommandIds(List<Command> commands) {
        commands.forEach(command -> commandManager.getCommands().get(command.getName()).setId(command.getIdLong()));
    }

    public boolean isDebugMode() {
        return debugMode;
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
