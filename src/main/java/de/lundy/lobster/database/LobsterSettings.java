package de.lundy.lobster.database;

import de.lundy.lobster.Lobster;
import de.lundy.lobster.exception.LobsterDatabaseException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LobsterSettings {

    public List<Long> getRegisteredServers() {

        Statement statement;

        try {

            statement = Lobster.getDatabase().getConnection().createStatement();
            statement.execute("select discordId from settings");
            var results = statement.getResultSet();
            var registeredServers = new ArrayList<Long>();

            while (results.next()) {
                registeredServers.add(results.getLong("discordId"));
            }

            results.close();
            statement.close();

            return registeredServers;

        } catch (SQLException e) {
            throw new LobsterDatabaseException("Could not get list of registered servers");
        }

    }

    public CompletableFuture registerServer(long discordId) {

        var statement = database.getConnection().prepareStatement("insert into settings (discordId) values (?)");
        statement.setLong(1, Long.parseLong(discordId));
        statement.execute();
        statement.close();

    }

    public String getPrefix(long discordId) {

        var statement = database.getConnection().prepareStatement("select prefix from settings where discordId=?");
        statement.setLong(1, Long.parseLong(discordId));
        statement.execute();
        var results = statement.getResultSet();
        var prefix = "!";

        while (results.next()) {
            prefix = results.getString("prefix");
        }

        results.close();
        statement.close();

        return prefix;

    }

    public void changePrefix(long discordId, String newPrefix) {

        var statement = database.getConnection().prepareStatement("update settings set prefix=? where discordId=?");
        statement.setString(1, prefix);
        statement.setLong(2, Long.parseLong(discordId));
        statement.execute();
        statement.close();

    }

}
