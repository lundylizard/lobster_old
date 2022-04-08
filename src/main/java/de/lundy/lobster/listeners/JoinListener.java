package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobsterbot;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinListener extends ListenerAdapter {

    private final SettingsManager settingsManager;

    public JoinListener(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {

        Lobsterbot.LOGGER.info("Joined Discord Server {} ({}) -- {}", event.getGuild().getName(), event.getGuild().getMemberCount(), event.getGuild().getIdLong());

        // Create guild tables on join
        if (!settingsManager.serverInSettingsTable(event.getGuild().getIdLong())) {
            Lobsterbot.LOGGER.info("{} is not in the database yet. Creating...", event.getGuild().getName());
            settingsManager.putServerIntoSettingsTable(event.getGuild().getIdLong(), "!");
        }

    }
}
