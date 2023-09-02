package me.lundy.lobster.config;

public enum ConfigValues {

    BOT_TOKEN("bot.token", "BOT_TOKEN"),
    SPOTIFY_CLIENT_ID("spotify.client_id", "SPOTIFY_CLIENT_ID"),
    SPOTIFY_CLIENT_SECRET("spotify.client_secret", "SPOTIFY_CLIENT_SECRET"),
    WEBHOOK_URL("webhook.url", "WEBHOOK_URL"),
    DATABASE_USER("database.user", "DATABASE_USER"),
    DATABASE_PASSWORD("database.password", "DATABASE_PASSWORD");

    public final String propertyPath;
    public final String defaultValue;

    ConfigValues(String propertyPath, String defaultValue) {
        this.propertyPath = propertyPath;
        this.defaultValue = defaultValue;
    }

}
