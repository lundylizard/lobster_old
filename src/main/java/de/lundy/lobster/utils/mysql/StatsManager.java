package de.lundy.lobster.utils.mysql;

import de.lundy.lobster.utils.MySQLUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class StatsManager {

    public HashMap<Long, Long> vcSession = new HashMap<>();

    //Creates a table in the mysql database to store the stats for each server
    public void generateStatsTable() throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.execute("create table if not exists stats (discord_id bigint(20), commands int, songs int, timePlayed bigint(20))");

    }

    //Check if there's already an entry for the server
    public boolean serverInStatsTable(long serverDiscordId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select discord_id from stats");
        var ids = new ArrayList<Long>();

        while (results.next()) {
            ids.add(results.getLong("discord_id"));
        }

        return ids.contains(serverDiscordId);

    }

    //Create an entry for the server in the settings table with a default prefix
    public void putServerIntoStatsTable(long serverId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("insert into stats values (" + serverId + ", 0, 0, 0)");

    }

    public int getCommandsExecuted(long serverId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select commands from stats where discord_id = " + serverId);
        var amount = 0;

        while (results.next()) {
            amount = results.getInt("commands");
        }

        return amount;

    }

    public void setCommandsExecuted(long serverId, int amount) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("update stats set commands = " + amount + " where discord_id= " + serverId);

    }

    public Integer[] getTotalCommandsExecuted() throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select commands from stats");
        var commandList = new ArrayList<Integer>();

        while (results.next()) {
            commandList.add(results.getInt("commands"));
        }

        return commandList.toArray(Integer[]::new);

    }

    public long getTimePlayed(long serverId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select timePlayed from stats where discord_id = " + serverId);
        var amount = 0L;

        while (results.next()) {
            amount = results.getLong("timePlayed");
        }

        return amount;

    }

    public void setTimePlayed(long serverId, long amount) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("update stats set timePlayed = " + amount + " where discord_id= " + serverId);

    }

    public Integer[] getTotalTimePlayed() throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select timePlayed from stats");
        var timeList = new ArrayList<Integer>();

        while (results.next()) {
            timeList.add(results.getInt("timePlayed"));
        }

        return timeList.toArray(Integer[]::new);

    }

    public long calculateTimePlayed(long serverId) {
        return (System.currentTimeMillis() / 1000) - vcSession.get(serverId);
    }

}
