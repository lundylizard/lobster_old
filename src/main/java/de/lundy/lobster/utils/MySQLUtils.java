package de.lundy.lobster.utils;

import de.lundy.lobster.Lobsterbot;
import de.lundy.lobster.Secrets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLUtils {

    public static Connection connection;
    private static Properties properties;

    private static Properties getProperties() {

        if (properties == null) {

            properties = new Properties();

            if (!Lobsterbot.DEBUG) {

                properties.setProperty("user", Secrets.DATABASE_USER);
                properties.setProperty("password", Secrets.DATABASE_PASSWORD);

            } else {

                properties.setProperty("user", "root");
                properties.setProperty("password", "");

            }

            properties.setProperty("MaxPooledStatements", "250");

        }

        return properties;

    }

    public static void connect() {

        if (connection == null) {
            try {

                connection = DriverManager.getConnection(Secrets.DATABASE_URL, getProperties());

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
