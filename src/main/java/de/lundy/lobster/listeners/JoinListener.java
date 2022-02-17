package de.lundy.lobster.listeners;

import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class JoinListener extends ListenerAdapter {

    private final SettingsManager settingsManager;

    public JoinListener(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {

        ChatUtils.print("JOIN: +" + event.getGuild().getName());

        try {

            // Create guild database on join
            if (!settingsManager.serverInSettingsTable(event.getGuild().getIdLong())) {
                ChatUtils.print("DATABASE: " + event.getGuild().getName() + " is not in the database yet. Creating...");
                settingsManager.putServerIntoSettingsTable(event.getGuild().getIdLong(), "!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
