package me.lundy.lobster.config;

public class BotConfig {

    private final String botToken;
    private final SpotifyConfig spotifyConfig;

    public BotConfig() {
        this.botToken = "";
        this.spotifyConfig = new SpotifyConfig();
    }

    public String getBotToken() {
        return botToken;
    }

    public SpotifyConfig getSpotifyConfig() {
        return spotifyConfig;
    }
}
