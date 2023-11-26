package me.lundy.lobster.database.settings;

import com.zaxxer.hikari.HikariDataSource;
import me.lundy.lobster.command.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandHistoryManager {

    public record CommandHistory(long guildId, long channelId, String command, String fullCommand, long usedTimestamp) {
        public CommandHistory(CommandContext context) {
            this(
                    context.getGuild().getIdLong(),
                    context.getEvent().getChannel().getIdLong(),
                    context.getEvent().getName(),
                    context.getEvent().getFullCommandName(),
                    System.currentTimeMillis()
            );
        }
        public CommandHistory(ResultSet resultSet) throws SQLException {
            this(
                    resultSet.getLong("guildId"),
                    resultSet.getLong("channelid"),
                    resultSet.getString("command"),
                    resultSet.getString("fullCommand"),
                    resultSet.getLong("usedTimestamp")
            );
        }
    }

    private final HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(GuildSettingsManager.class);
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS commandhistory(guildId BIGINT NOT NULL, channelId BIGINT NOT NULL, command TEXT NOT NULL, fullCommand TEXT NOT NULL, usedTimestamp BIGINT NOT NULL);";
    private static final String INSERT_QUERY = "INSERT INTO commandhistory (guildid, channelid, command, fullCommand, usedTimestamp) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_FROM_GUILD_QUERY = "SELECT * FROM commandhistory WHERE guildid = ?";

    private final PreparedStatement insertStatement;
    private final PreparedStatement getFromGuildStatement;

    public CommandHistoryManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.createCommandHistoryTable();
        this.insertStatement = prepareStatement(INSERT_QUERY);
        this.getFromGuildStatement = prepareStatement(GET_FROM_GUILD_QUERY);
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

    private void createCommandHistoryTable() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error creating command history table: {}", e.getMessage(), e);
        }
    }

    public void insertCommandHistory(CommandHistory commandHistory) {
        try {
            insertStatement.setLong(1, commandHistory.guildId);
            insertStatement.setLong(2, commandHistory.channelId);
            insertStatement.setString(3, commandHistory.command);
            insertStatement.setString(4, commandHistory.fullCommand);
            insertStatement.setLong(5, commandHistory.usedTimestamp);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting command history: {}", e.getMessage(), e);
        }
    }

    public List<CommandHistory> getCommandHistoryFromGuild(long guildId) {
        List<CommandHistory> commandHistories = new ArrayList<>();
        try {
            getFromGuildStatement.setLong(1, guildId);
            try (ResultSet resultSet = getFromGuildStatement.executeQuery()) {
                while (resultSet.next()) {
                    commandHistories.add(new CommandHistory(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting command history from guild with ID {}: {}", guildId, e.getMessage(), e);
        }
        return commandHistories;
    }

}
