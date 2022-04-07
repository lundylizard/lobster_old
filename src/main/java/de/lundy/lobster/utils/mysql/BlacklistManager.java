package de.lundy.lobster.utils.mysql;

import de.lundy.lobster.utils.MySQLUtils;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlacklistManager {

    private final MySQLUtils database;

    public BlacklistManager(MySQLUtils database) {
        this.database = database;
        generateBlacklistTable();
    }

    //Creates a table in the mysql database to store the prefix for each server
    private void generateBlacklistTable() {

        try {

            var statement = database.getConnection().createStatement();
            statement.execute("create table if not exists blacklist (discord_id bigint(20), reason text)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Check if there's already an entry for the server
    public boolean serverInBlacklistTable(long discordId) {

        try {

            var statement = database.getConnection().createStatement();
            var results = statement.executeQuery("select discord_id from blacklist");
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

    @Nullable
    public String getBlacklistReason(long discordId) {

        if (!serverInBlacklistTable(discordId)) {
            return null;
        }

        try {

            var statement = database.getConnection().createStatement();
            var results = statement.executeQuery("select reason from blacklist where discord_id = " + discordId);
            var reason = "";

            while (results.next()) {
                reason = results.getString("reason");
            }

            return reason;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    //Create an entry for the server in the blacklist table with a reason
    public void putServerInBlacklistTable(long discordId, String reason) {

        try {

            var statement = database.getConnection().createStatement();
            statement.executeUpdate("insert into blacklist values (" + discordId + ", \"" + reason + "\")");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeServerFromBlacklistTable(long discordId) {

        try {

            var statement = database.getConnection().createStatement();
            statement.executeUpdate("delete from blacklist where discord_id = " + discordId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
