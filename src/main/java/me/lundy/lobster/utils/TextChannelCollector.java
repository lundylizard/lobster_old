package me.lundy.lobster.utils;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class collects the last text channel used by lobster on a guild.
 * Used for update announcements and such. lobster is not collecting your conversations.
 */
public class TextChannelCollector {

    // Key = Guild ID ; Value = Text Channel ID
    private final Map<Long, Long> textChannelIds = new ConcurrentHashMap<>();

    public void addTextChannel(TextChannel textChannel) {
        textChannelIds.compute(textChannel.getGuild().getIdLong(), (k, v) -> textChannel.getIdLong());
    }

    public Map<Long, Long> getTextChannelIds() {
        return textChannelIds;
    }

}
