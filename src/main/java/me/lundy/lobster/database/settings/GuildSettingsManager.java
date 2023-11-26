package me.lundy.lobster.database.settings;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GuildSettingsManager {

    private final HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(GuildSettingsManager.class);
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS guildSettings(guildId BIGINT NOT NULL PRIMARY KEY, lastChannelUsedId BIGINT NOT NULL DEFAULT -1);";
    private static final String UPSERT_CHANNEL_ID_QUERY = "INSERT INTO guildsettings (guildid, lastchannelusedid) VALUES (?, ?) ON CONFLICT (guildid) DO UPDATE SET lastchannelusedid = ?";
    private static final String GET_CHANNEL_FROM_GUILD_QUERY = "SELECT lastchannelusedid FROM guildsettings WHERE guildid = ?";

    public GuildSettingsManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.createGuildSettings();
    }

    private void createGuildSettings() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error creating guild settings table: {}", e.getMessage(), e);
        }
    }

    public boolean upsertLastChannelUsedId(long guildId, long lastUsedChannelId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPSERT_CHANNEL_ID_QUERY)) {
            statement.setLong(1, guildId);
            statement.setLong(2, lastUsedChannelId);
            statement.setLong(3, lastUsedChannelId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error upserting last channel used id: {}", e.getMessage(), e);
            return false;
        }
    }

    public long getLastChannelUsedId(long guildId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_CHANNEL_FROM_GUILD_QUERY)) {
            statement.setLong(1, guildId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong("lastchannelusedid") : 0L;
            }
        } catch (SQLException e) {
            logger.error("Error getting last channel used id: {}", e.getMessage(), e);
            return 0L;
        }
    }

}
