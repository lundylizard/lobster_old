package de.lundy.lobster.database;

import de.lundy.lobster.exception.LobsterDatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class LobsterDatabase {

    private static final String user = "root";
    private static final String url = "jdbc:mysql://localhost:3306/lobster";
    private final String password;

    private Connection connection;

    public LobsterDatabase(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {

        if (this.connection != null) {
            return this.connection;
        }

        Properties properties = new Properties();
        properties.setProperty("user", getUser());
        properties.setProperty("password", getPassword());

        try {
            this.connection = DriverManager.getConnection(getUrl(), properties);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return this.connection;

    }

    public LobsterBlacklist getBlacklist() {
        return new LobsterBlacklist();
    }

    public LobsterSettings getSettings() {
        return new LobsterSettings();
    }

    public void createTables() {

        Statement statement;

        try {

            statement = getConnection().createStatement();
            statement.execute("create table if not exists blacklist (discordId bigint(20) not null, reason text not null)");
            statement.execute("create table if not exists settings (discordId bigint(20) not null, prefix text not null default '!')");
            statement.close();

        } catch (SQLException e) {
            throw new LobsterDatabaseException("Could not create blacklist table");
        }

    }

}
