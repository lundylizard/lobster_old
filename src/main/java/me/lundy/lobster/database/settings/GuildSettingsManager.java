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
    private final PreparedStatement upsertStatement;
    private final PreparedStatement getChannelStatement;

    public GuildSettingsManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.createGuildSettingsTable();
        this.upsertStatement = prepareStatement(UPSERT_CHANNEL_ID_QUERY);
        this.getChannelStatement = prepareStatement(GET_CHANNEL_FROM_GUILD_QUERY);
    }

    private PreparedStatement prepareStatement(String query) {
        try {
            Connection connection = dataSource.getConnection();
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            logger.error("Error preparing statement: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to prepare statement", e);
        }
    }

    private void createGuildSettingsTable() {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(CREATE_TABLE_QUERY);
            }
        } catch (SQLException e) {
            logger.error("Error creating guild settings table: {}", e.getMessage(), e);
        }
    }

    public void upsertLastChannelUsedId(long guildId, long lastUsedChannelId) {
        try {
            upsertStatement.setLong(1, guildId);
            upsertStatement.setLong(2, lastUsedChannelId);
            upsertStatement.setLong(3, lastUsedChannelId);
            upsertStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error upserting last channel used id: {}", e.getMessage(), e);
        }
    }

    public long getLastChannelUsedId(long guildId) {
        try {
            getChannelStatement.setLong(1, guildId);
            try (ResultSet resultSet = getChannelStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong("lastchannelusedid") : 0L;
            }
        } catch (SQLException e) {
            logger.error("Error getting last channel used id: {}", e.getMessage(), e);
            return 0L;
        }
    }

}
