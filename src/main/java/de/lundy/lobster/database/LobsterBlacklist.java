package de.lundy.lobster.database;

import de.lundy.lobster.Lobster;
import de.lundy.lobster.exception.LobsterDatabaseException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class LobsterBlacklist {

    public Map<Long, String> getBlacklist() {

        Statement statement;

        try {

            statement = Lobster.getDatabase().getConnection().createStatement();
            statement.execute("select * from blacklist");
            var results = statement.getResultSet();
            var blacklist = new HashMap<Long, String>();

            while (results.next()) {
                blacklist.put(results.getLong("discordId"), results.getString("reason"));
            }

            results.close();
            statement.close();

            return blacklist;

        } catch (SQLException e) {
            throw new LobsterDatabaseException("Could not retrieve blacklist");
        }

    }

    public void addToBlacklist(long discordId, String reason) {

        PreparedStatement statement;

        try {

            statement = Lobster.getDatabase().getConnection().prepareStatement("insert into blacklist values (?, ?)");
            statement.setLong(1, discordId);
            statement.setString(2, reason);
            statement.execute();
            statement.close();

        } catch (SQLException e) {
            throw new LobsterDatabaseException("Could not add server with id %d to the blacklist", discordId);
        }

    }

    public void removeFromBlacklist(long discordId) {

        PreparedStatement statement;

        try {

            statement = Lobster.getDatabase().getConnection().prepareStatement("delete from blacklist where discordId=?");
            statement.setLong(1, discordId);
            statement.execute();
            statement.close();

        } catch (SQLException e) {
            throw new LobsterDatabaseException("Could not remove server with id %d from the blacklist", discordId);
        }

    }

}
