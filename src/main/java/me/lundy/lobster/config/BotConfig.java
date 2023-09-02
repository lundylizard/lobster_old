package me.lundy.lobster.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class BotConfig {

    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);
    private static BotConfig instance;
    private final String absoluteConfigPath;
    private final Properties properties;

    private BotConfig(File configFile) throws IOException {

        this.absoluteConfigPath = configFile.getAbsolutePath();
        properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            logger.error("Failed to load configuration file", e);
            throw e;
        }

        List<ConfigProperty> propertyList = new ArrayList<>();
        for (ConfigValues values : ConfigValues.values()) {
            propertyList.add(new ConfigProperty(values.propertyPath, values.defaultValue));
        }

        for (ConfigProperty property : propertyList) {
            if (!properties.containsKey(property.path())) {
                properties.put(property.path(), property.defaultValue());
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(configFile)) {
            properties.store(fileOutputStream, "Bot Configuration File");
        } catch (IOException e) {
            logger.error("Failed to save configuration file", e);
        }

    }

    public static BotConfig getInstance() {

        if (instance != null) {
            return instance;
        }

        File configFile = new File("bot.properties");

        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    logger.info("Created bot.properties file");
                }
            } catch (IOException e) {
                logger.error("An error occurred while creating a config file", e);
                return null;
            }
        }

        try {
            instance = new BotConfig(configFile);
        } catch (IOException e) {
            logger.error("Could not create config object instance", e);
        }
        return instance;
    }

    public static BotConfig getInstance(File configFile) throws IOException {
        if (instance == null || !instance.absoluteConfigPath.equals(configFile.getAbsolutePath())) {
            instance = new BotConfig(configFile);
        }
        return instance;
    }

    public String getProperty(ConfigValues configValue) {
        String value = this.properties.getProperty(configValue.propertyPath);
        return Optional.ofNullable(value)
                .filter(v -> !v.equals(configValue.defaultValue))
                .orElseThrow(() -> new PropertyDefaultException("Config value " + configValue.propertyPath + " is not set."));
    }

}
