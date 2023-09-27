package me.lundy.lobster.handler;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresenceHandler extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PresenceHandler.class);

    public void updateActivity(ShardManager shardManager) {
        int serverCount = shardManager.getGuilds().size();
        Activity activity = Activity.customStatus(String.format("on %d servers", serverCount));
        shardManager.setActivity(activity);
        logger.info("Updated activity to '{}'", activity.getName());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        ShardManager shardManager = event.getJDA().getShardManager();
        if (shardManager == null) {
            logger.error("ShardManager in PresenceHandler is null, cannot update activity");
            return;
        }
        updateActivity(shardManager);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        ShardManager shardManager = event.getJDA().getShardManager();
        if (shardManager == null) {
            logger.error("ShardManager in PresenceHandler is null, cannot update activity");
            return;
        }
        updateActivity(shardManager);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ShardManager shardManager = event.getJDA().getShardManager();
        if (shardManager == null) {
            logger.error("ShardManager in PresenceHandler is null, cannot update activity");
            return;
        }
        updateActivity(shardManager);
    }
}
