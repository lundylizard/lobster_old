package de.lundy.lobster.utils;

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
            properties.setProperty("user", Secrets.DATABASE_USER);
            properties.setProperty("password", Secrets.DATABASE_PASSWORD);
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
