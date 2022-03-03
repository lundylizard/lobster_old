package de.lundy.lobster.listeners;

import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import de.lundy.lobster.utils.mysql.StatsManager;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class JoinListener extends ListenerAdapter {

    private final SettingsManager settingsManager;
    private final StatsManager statsManager;

    public JoinListener(SettingsManager settingsManager, StatsManager statsManager) {
        this.settingsManager = settingsManager;
        this.statsManager = statsManager;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {

        ChatUtils.print("JOIN: +" + event.getGuild().getName());

        try {

            // Create guild tables on join
            if (!settingsManager.serverInSettingsTable(event.getGuild().getIdLong())) {
                ChatUtils.print("DATABASE: " + event.getGuild().getName() + " is not in the database yet. Creating...");
                settingsManager.putServerIntoSettingsTable(event.getGuild().getIdLong(), "!");
            }

            if (!statsManager.serverInStatsTable(event.getGuild().getIdLong())) {
                ChatUtils.print("DATABASE: " + event.getGuild().getName() + " is not in the stats database yet. Creating...");
                statsManager.putServerIntoStatsTable(event.getGuild().getIdLong());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
