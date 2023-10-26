package me.lundy.lobster.database.settings;

public class GuildSettings {

    private long guildId;
    private long lastChannelUsedId;
    private boolean keepVolume;
    private int volume;
    private String embedColor;
    private boolean betaFeatures;
    private boolean collectStatistics;
    private boolean updateNotifications;

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

    public boolean isKeepVolume() {
        return keepVolume;
    }

    public void setKeepVolume(boolean keepVolume) {
        this.keepVolume = keepVolume;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getEmbedColor() {
        return embedColor;
    }

    public void setEmbedColor(String embedColor) {
        this.embedColor = embedColor;
    }

    public boolean isBetaFeatures() {
        return betaFeatures;
    }

    public void setBetaFeatures(boolean betaFeatures) {
        this.betaFeatures = betaFeatures;
    }

    public boolean isCollectStatistics() {
        return collectStatistics;
    }

    public void setCollectStatistics(boolean collectStatistics) {
        this.collectStatistics = collectStatistics;
    }

    public boolean isUpdateNotifications() {
        return updateNotifications;
    }

    public void setUpdateNotifications(boolean updateNotifications) {
        this.updateNotifications = updateNotifications;
    }
}
