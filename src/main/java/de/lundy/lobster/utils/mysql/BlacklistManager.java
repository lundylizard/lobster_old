package de.lundy.lobster.utils.mysql;

import de.lundy.lobster.utils.MySQLUtils;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlacklistManager {

    //Creates a table in the mysql database to store the prefix for each server
    public void generateBlacklistTable() throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.execute("create table if not exists blacklist (discord_id bigint(20), reason text)");

    }

    //Check if there's already an entry for the server
    public boolean serverInBlacklistTable(long serverDiscordId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select discord_id from blacklist");
        var ids = new ArrayList<Long>();

        while (results.next()) {
            ids.add(results.getLong("discord_id"));
        }

        return ids.contains(serverDiscordId);

    }

    @Nullable
    public String getBlacklistReason(long serverId) throws SQLException {

        if (!serverInBlacklistTable(serverId)) {
            return null;
        }

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        var results = statement.executeQuery("select reason from blacklist where discord_id = " + serverId);
        var reason = "";

        while (results.next()) {
            reason = results.getString("reason");
        }

        return reason;

    }

    //Create an entry for the server in the blacklist table with a reason
    public void putServerInBlacklistTable(long serverId, String reason) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("insert into blacklist values (" + serverId + ", \"" + reason + "\")");

    }

    public void removeServerFromBlacklistTable(long serverId) throws SQLException {

        MySQLUtils.connect();
        var statement = MySQLUtils.connection.createStatement();
        statement.executeUpdate("delete from blacklist where discord_id = " + serverId);

    }

}
