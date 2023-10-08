package me.lundy.lobster.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotConfig {

    private final String botToken;
    private final SpotifyConfig spotifyConfig;
    private final int configVersion;
    private final DatabaseConfig databaseConfig;
    private final Logger logger = LoggerFactory.getLogger(BotConfig.class);

    public BotConfig() {
        this.botToken = "";
        this.configVersion = ConfigManager.CURRENT_VERSION;
        this.spotifyConfig = new SpotifyConfig();
        this.databaseConfig = new DatabaseConfig();
    }

    public String getBotToken() {
        return botToken;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public SpotifyConfig getSpotifyConfig() {
        return spotifyConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
