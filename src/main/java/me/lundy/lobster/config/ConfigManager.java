package me.lundy.lobster.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {

    private static ConfigManager instance;
    private final ObjectMapper objectMapper;
    private final String fileName = "config.json";
    private final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    public ConfigManager() {
        this.objectMapper = new ObjectMapper();
    }

    public boolean createEmptyFile() {
        if (Files.notExists(Paths.get(this.fileName))) {
            BotConfig botConfig = new BotConfig();
            try {
                this.objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(this.fileName), botConfig);
                return true;
            } catch (IOException e) {
                this.logger.error("Could not write empty config file", e);
                return false;
            }
        }
        return false;
    }

    public BotConfig getBotConfig() {
        File configFile = new File(this.fileName);
        try {
            return this.objectMapper.readValue(configFile, BotConfig.class);
        } catch (IOException e) {
            this.logger.error("Could not instantiate bot config from config file", e);
            this.createEmptyFile();
            return getBotConfig();
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

}
