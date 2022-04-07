package de.lundy.lobster.utils;

import de.lundy.lobster.Lobsterbot;
import de.lundy.lobster.Secrets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLUtils {

    private Connection connection;
    private Properties properties;

    private Properties getProperties() {

        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", Lobsterbot.DEBUG ? "root" : Secrets.DATABASE_USER);
            properties.setProperty("password", Lobsterbot.DEBUG ? "root" : Secrets.DATABASE_PASSWORD);
            properties.setProperty("MaxPooledStatements", "250");
        }

        return properties;

    }

    public Connection getConnection() {

        if (connection != null) {
            return connection;
        }

        try {
            connection = DriverManager.getConnection(Secrets.DATABASE_URL, getProperties());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;

    }

}
