package me.lundy.lobster.config;

public class ConfigProperty {

    private final String path;
    private final String defaultValue;

    public ConfigProperty(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
