package me.lundy.lobster.database.settings;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

    private final HikariDataSource dataSource;
    private final long guildId;
    private final Logger logger = LoggerFactory.getLogger(SettingsManager.class);
    private final Map<String, Setting<?>> settings = new HashMap<>();

    public SettingsManager(HikariDataSource dataSource, long guildId) {
        this.dataSource = dataSource;
        this.guildId = guildId;
        createSetting(new Setting<>("keepVolume", "Keep Volume", ""), Boolean.class);
        createSetting(new Setting<>("collectStatistics", "Collect Statistics", ""), Boolean.class);
        createSetting(new Setting<>("embedColor", "Embed Color", ""), String.class);
    }

    public Map<String, Setting<?>> getSettings() {
        return settings;
    }

    private <T> void createSetting(Setting<T> setting, T type) {
        setting.setType(type);
        this.settings.put(setting.getPath(), setting);
    }

    public Setting<?> getSetting(String path) throws SQLException {
        Setting<?> setting = this.settings.get(path);
        if (setting == null) {
            throw new IllegalStateException("Tried to get setting " + path + " but it doesn't exist?");
        }
        String sql = String.format("SELECT %s FROM settings WHERE guildId = ?", path);
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, this.guildId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    setting.setValue(resultSet.getObject(path));
                    return setting;
                }
            }
        }
        return null;
    }

    public void initializeSettings(long guildId) throws SQLException {
        String sql = "INSERT INTO settings (guildId) VALUES (?) ON CONFLICT (guildId) DO NOTHING";
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, guildId);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) logger.info("Created settings for guild with ID {}", guildId);
        }
    }

    public GuildSettings getSettings(long guildId) throws SQLException {
        String sql = "SELECT * FROM settings WHERE guildId = ?";
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, guildId);
            try (ResultSet resultSet = statement.executeQuery()) {
                GuildSettings settings = new GuildSettings();
                if (resultSet.next()) {
                    settings.setGuildId(guildId);
                    settings.setLastChannelUsedId(resultSet.getLong("lastChannelUsedId"));
                    settings.setKeepVolume(resultSet.getBoolean("keepVolume"));
                    settings.setEmbedColor(resultSet.getString("embedColor"));
                    settings.setBetaFeatures(resultSet.getBoolean("betaFeatures"));
                    settings.setCollectStatistics(resultSet.getBoolean("collectStatistics"));
                    settings.setUpdateNotifications(resultSet.getBoolean("updateNotifications"));
                    settings.setVolume(resultSet.getInt("volume"));
                }
                return settings;
            }
        }
    }

    public void updateSettings(long guildId, GuildSettings setting) throws SQLException {
        String sql = "UPDATE settings SET guildId = ?, lastChannelUsedId = ?, keepVolume = ?, volume = ?, embedColor = ?, betaFeatures = ?, collectStatistics = ?, updateNotifications = ? WHERE guildId = ?";
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, setting.getGuildId());
            preparedStatement.setLong(2, setting.getLastChannelUsedId());
            preparedStatement.setBoolean(3, setting.isKeepVolume());
            preparedStatement.setInt(4, setting.getVolume());
            preparedStatement.setString(5, setting.getEmbedColor());
            preparedStatement.setBoolean(6, setting.isBetaFeatures());
            preparedStatement.setBoolean(7, setting.isCollectStatistics());
            preparedStatement.setBoolean(8, setting.isUpdateNotifications());
            preparedStatement.setLong(9, guildId);
            preparedStatement.executeUpdate();
        }
    }

}
