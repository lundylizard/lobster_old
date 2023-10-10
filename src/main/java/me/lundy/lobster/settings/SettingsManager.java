package me.lundy.lobster.settings;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SettingsManager {

    private final HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(SettingsManager.class);

    public SettingsManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
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
