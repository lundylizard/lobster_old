package me.lundy.lobster.settings;

public enum Settings {

    KEEP_VOLUME("Keep Volume", "keepVolume"),
    EMBED_COLOR("Embed Color", "embedColor"),
    ACTIVATE_BETA_FEATURES("Activate Beta Features", "betaFeatures"),
    COLLECT_STATISTICS("Collect Statistics", "collectStatistics"),
    UPDATE_NOTIFICATIONS("Update Notifications", "updateNotifications");

    private final String friendlyName;
    private final String name;

    Settings(String friendlyName, String name) {
        this.friendlyName = friendlyName;
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getName() {
        return name.toLowerCase(); // Make it lowercase programmatically because I'm lazy
    }
}
