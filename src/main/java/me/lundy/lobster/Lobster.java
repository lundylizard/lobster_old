package me.lundy.lobster;

import me.lundy.lobster.command.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigManager;
import me.lundy.lobster.database.DatabaseManager;
import me.lundy.lobster.handler.InactivityHandler;
import me.lundy.lobster.handler.PresenceHandler;
import me.lundy.lobster.listeners.VoiceDisconnectListener;
import me.lundy.lobster.listeners.buttons.QueueButtonListener;
import me.lundy.lobster.settings.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

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
    private SettingsManager settingsManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        instance = new Lobster();
        commandManager = new CommandManager();
        instance.configManager = ConfigManager.getInstance();
        instance.database = new DatabaseManager();
        instance.settingsManager = new SettingsManager(instance.database.getDataSource());
        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createLight(instance.getConfig().getBotToken());
        shardManagerBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardManagerBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardManagerBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);
        shardManagerBuilder.addEventListeners(new QueueButtonListener());
        shardManagerBuilder.addEventListeners(new PresenceHandler(instance.settingsManager));
        shardManagerBuilder.addEventListeners(new VoiceDisconnectListener());
        shardManagerBuilder.addEventListeners(commandManager);
        ShardManager shardManager = shardManagerBuilder.build();
        JDA rootShard = shardManager.getShards().get(0);
        rootShard.setRequiredScopes("applications.commands");
        rootShard.updateCommands().addCommands(commandManager.getCommandDataList()).queue(Lobster::setCommandIds);
        instance.scheduler.scheduleWithFixedDelay(() -> instance.tick(shardManager), 0, 1, TimeUnit.MINUTES);
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

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
}
