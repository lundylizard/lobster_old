package me.lundy.lobster.config;

public class SpotifyConfig {

    private final String clientSecret;
    private final String clientId;
    private final String countryCode;

    public SpotifyConfig() {
        this.clientId = "";
        this.clientSecret = "";
        this.countryCode = "";
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
