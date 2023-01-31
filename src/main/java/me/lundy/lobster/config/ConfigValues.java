package me.lundy.lobster.config;

public enum ConfigValues {

    BOT_TOKEN("bot.token", "BOT_TOKEN"),
    SPOTIFY_CLIENT_ID("spotify.client_id", "SPOTIFY_CLIENT_ID"),
    SPOTIFY_CLIENT_SECRET("spotify.client_secret", "SPOTIFY_CLIENT_SECRET"),
    APPLE_MEDIA_API_TOKEN("apple.token", "APPLE_MEDIA_API_TOKEN");

    public final String propertyPath;
    public final String defaultValue;

    ConfigValues(String propertyPath, String defaultValue) {
        this.propertyPath = propertyPath;
        this.defaultValue = defaultValue;
    }

}
