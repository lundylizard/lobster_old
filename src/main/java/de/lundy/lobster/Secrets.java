package de.lundy.lobster;

public enum Secrets {

    DATABASE_URL("jdbc:mysql://localhost:3306/lobster "),
    DATABASE_PASSWORD(""),
    SPOTIFY_CLIENT_TOKEN("a508e2cb00934248adfd135953a9b62e"),
    SPOTIFY_CLIENT_ID("1a0e7d17c77b46749c1d3ec65fdf574a");

    private final String value;

    Secrets(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
