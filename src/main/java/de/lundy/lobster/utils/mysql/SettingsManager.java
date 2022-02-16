package de.lundy.lobster.utils.mysql;

import de.lundy.lobster.utils.MySQLUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class SettingsManager {

    //Creates a table in the mysql database to store the prefix for each server
    public void generateSettingsTable() throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.execute("create table if not exists settings (discord_id bigint(20), prefix text)");

    }

    //Check if there's already an entry for the server
    public boolean serverInSettingsTable(long serverDiscordId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select discord_id from settings");
        var ids = new ArrayList<Long>();

        while (results.next()) {
            ids.add(results.getLong("discord_id"));
        }

        return ids.contains(serverDiscordId);

    }

    //Create an entry for the server in the settings table with a default prefix
    public void putServerIntoSettingsTable(long serverId, String defaultPrefix) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("insert into settings values (" + serverId + ", \"" + defaultPrefix + "\")");

    }

    //Returns the prefix from a server from the mysql database
    public String getPrefix(long serverId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select prefix from settings where discord_id = " + serverId);

        while (results.next()) {
            return results.getString("prefix");
        }

        return null;

    }

    //Updates the entry for the prefix for a server in the mysql database
    public void setPrefix(long serverId, String newPrefix) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("update settings set prefix = \"" + newPrefix + "\" where discord_id = " + serverId);

    }

}
