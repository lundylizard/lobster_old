package de.lundy.lobster.utils.mysql;

import de.lundy.lobster.Lobsterbot;
import de.lundy.lobster.utils.MySQLUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public record SettingsManager(MySQLUtils database) {

    public SettingsManager(MySQLUtils database) {
        this.database = database;
        generateSettingsTable();
    }

    //Creates a table in the mysql database to store the prefix for each server
    private void generateSettingsTable() {

        try {

            var statement = database.getConnection().createStatement();
            statement.execute("create table if not exists settings (discord_id bigint(20), prefix text)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Check if there's already an entry for the server
    public boolean serverInSettingsTable(long discordId) {

        try {

            var statement = database.getConnection().createStatement();
            var results = statement.executeQuery("select discord_id from settings");
            var ids = new ArrayList<Long>();

            while (results.next()) {
                ids.add(results.getLong("discord_id"));
            }

            return ids.contains(discordId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

    //Create an entry for the server in the settings table with a default prefix
    public void putServerIntoSettingsTable(long discordId, String defaultPrefix) {

        try {

            var statement = database.getConnection().createStatement();
            statement.executeUpdate("insert into settings values (" + discordId + ", \"" + defaultPrefix + "\")");

            Lobsterbot.LOGGER.info("Created database entry for {}", discordId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Returns the prefix from a server from the mysql database
    public String getPrefix(long discordId) {

        try {

            var statement = database.getConnection().createStatement();
            var results = statement.executeQuery("select prefix from settings where discord_id = " + discordId);

            while (results.next()) {
                return results.getString("prefix");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "!"; // Default Prefix in case something's up

    }

    //Updates the entry for the prefix for a server in the mysql database
    public void setPrefix(long discordId, String newPrefix) {

        try {

            var statement = database.getConnection().createStatement();
            statement.executeUpdate("update settings set prefix = \"" + newPrefix + "\" where discord_id = " + discordId);
            Lobsterbot.LOGGER.info("Changed prefix in {} to {}", discordId, newPrefix);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void resetSettings(long discordId) {

        setPrefix(discordId, "!");

    }

}
