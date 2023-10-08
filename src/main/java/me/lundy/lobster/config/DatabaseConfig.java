package me.lundy.lobster.config;

public class DatabaseConfig {

    private final String username;
    private final String password;
    private final String url;

    public DatabaseConfig() {
        this.username = "";
        this.password = "";
        this.url = "";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}
