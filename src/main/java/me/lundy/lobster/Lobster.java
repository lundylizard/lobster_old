package me.lundy.lobster;

import me.lundy.lobster.command.CommandManager;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.ConfigValues;
import me.lundy.lobster.internal.InactivityHandler;
import me.lundy.lobster.internal.PresenceHandler;
import me.lundy.lobster.listeners.VoiceDisconnectListener;
import me.lundy.lobster.listeners.buttons.QueueButtonListener;
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

    public static final String INVITE_URL = "https://discord.com/api/oauth2/authorize?" +
            "client_id=891760327522394183" +
            "&permissions=2150647808" +
            "&scope=bot%20applications.commands";
    public static final String DISCORD_URL = "https://discord.gg/Hk5YP5AWhW";

    private static Lobster instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private BotConfig config;
    private CommandManager commandManager;

    // TODO
    // - Update config system (use json instead)
    // - Better pagination system for queue command

    public static void main(String[] args) {
        instance = new Lobster();
        instance.config = BotConfig.getInstance();
        instance.commandManager = new CommandManager();
        String botToken = instance.config.getProperty(ConfigValues.BOT_TOKEN);
        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createLight(botToken);
        shardManagerBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
        shardManagerBuilder.enableCache(CacheFlag.VOICE_STATE);
        shardManagerBuilder.setMemberCachePolicy(MemberCachePolicy.VOICE);
        shardManagerBuilder.addEventListeners(new QueueButtonListener());
        shardManagerBuilder.addEventListeners(new PresenceHandler());
        shardManagerBuilder.addEventListeners(new VoiceDisconnectListener());
        shardManagerBuilder.addEventListeners(instance.commandManager);
        ShardManager shardManager = shardManagerBuilder.build();
        JDA rootShard = shardManager.getShards().get(0);
        rootShard.updateCommands().addCommands(instance.commandManager.getCommandDataList()).queue(Lobster::setCommandIds);
        instance.scheduler.scheduleWithFixedDelay(() -> instance.tick(shardManager), 0, 1, TimeUnit.MINUTES);
    }

    private void tick(ShardManager shardManager) {
        handleInactivity(shardManager);
    }

    private void handleInactivity(ShardManager shardManager) {
        shardManager.getGuilds().forEach(InactivityHandler::handleInactivity);
    }

    private static void setCommandIds(List<Command> commands) {
        commands.forEach(command -> instance.commandManager.getCommands().get(command.getName()).setId(command.getIdLong()));
    }

    public BotConfig getConfig() {
        return config;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static Lobster getInstance() {
        return instance;
    }

}
