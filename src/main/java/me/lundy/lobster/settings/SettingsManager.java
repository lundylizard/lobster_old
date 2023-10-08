package me.lundy.lobster.settings;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

    private final HikariDataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(SettingsManager.class);

    public SettingsManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, Object> getSettingsFromGuild(long guildId) throws SQLException {
        Connection connection = this.dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM settings WHERE guildId = ?");
        statement.setLong(1, guildId);
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<String, Object> settings = new HashMap<>();
        resultSet.next();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            settings.put(columnName, resultSet.getObject(columnName));
        }
        return settings;
    }

    public void createSettingsForGuild(long guildId, long channelId) throws SQLException {
        Connection connection = this.dataSource.getConnection();
        String sql = "INSERT INTO settings (guildid, lastchannelusedid) VALUES (?, ?) ON CONFLICT (guildid) DO NOTHING";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, guildId);
        preparedStatement.setLong(2, channelId);
        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            logger.info("Created settings for guild with ID {} (Rows Inserted: {})", guildId, rowsInserted);
        }
    }

    public void updateSettingOfGuild(long guildId, Settings setting, String value) throws SQLException {
        Connection connection = this.dataSource.getConnection();
        String sql = "UPDATE settings SET " + setting.getName() + " = ? WHERE guildId = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if (value.equals("TRUE") || value.equals("FALSE")) {
            preparedStatement.setBoolean(1, Boolean.parseBoolean(value));
        } else {
            preparedStatement.setString(1, value);
        }
        preparedStatement.setLong(2, guildId);
        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            logger.info("Changed setting {} from guild with ID {} to {}", setting.getFriendlyName(), guildId, value);
        }
    }

}
