package me.lundy.lobster.config;

public class BotConfig {

    private final String botToken;
    private final SpotifyConfig spotifyConfig;
    private final DatabaseConfig databaseConfig;

    public BotConfig() {
        this.botToken = "";
        this.spotifyConfig = new SpotifyConfig();
        this.databaseConfig = new DatabaseConfig();
    }

    public String getBotToken() {
        return botToken;
    }

    public SpotifyConfig getSpotifyConfig() {
        return spotifyConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
