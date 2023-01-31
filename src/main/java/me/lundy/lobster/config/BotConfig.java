package me.lundy.lobster.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BotConfig {

    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);
    private static BotConfig instance;
    private final String absoluteConfigPath;
    private final List<ConfigProperty> propertyList = new ArrayList<>();
    private final Properties properties;

    private BotConfig(File configFile) {

        this.absoluteConfigPath = configFile.getAbsolutePath();
        properties = new Properties();

        try {
            properties.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ConfigValues values : ConfigValues.values()) {
            this.propertyList.add(new ConfigProperty(values.propertyPath, values.defaultValue));
        }

        for (ConfigProperty property : this.propertyList) {
            if (!properties.containsKey(property.getPath()))
                properties.put(property.getPath(), property.getDefaultValue());
        }

        try {
            properties.store(new FileOutputStream(configFile), "Bot Configuration File");
        } catch (IOException e) {
            logger.error("Exception occurred", e);
        }

    }

    public static BotConfig getInstance() {
        if (instance != null) return instance;
        File configFile = new File("bot.properties");
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    logger.info("Created bot.properties file");
                }
            } catch (IOException e) {
                logger.error("Exception occurred", e);
            }
        }
        instance = new BotConfig(configFile);
        return instance;
    }

    public static BotConfig getInstance(File configFile) {
        if (instance == null || !instance.absoluteConfigPath.equals(configFile.getAbsolutePath())) {
            instance = new BotConfig(configFile);
        }
        return instance;
    }

    public String getProperty(ConfigValues configValue) {
        ConfigProperty property = this.propertyList.stream().filter(p -> p.getPath().equals(configValue.propertyPath)).toList().get(0);
        if (this.properties.getProperty(configValue.propertyPath).equals(property.getDefaultValue())) {
            throw new PropertyDefaultException("Config value " + configValue.propertyPath + " is not set.");
        }
        return this.properties.getProperty(configValue.propertyPath);
    }

}
