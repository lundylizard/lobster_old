package me.lundy.lobster.database.settings;

import me.lundy.lobster.database.Id;
import me.lundy.lobster.database.Table;

@Table(name = "guildSettings")
public class GuildSettings {

    @Id
    private long guildId;
    private long lastChannelUsedId;

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getLastChannelUsedId() {
        return lastChannelUsedId;
    }

    public void setLastChannelUsedId(long lastChannelUsedId) {
        this.lastChannelUsedId = lastChannelUsedId;
    }

}
