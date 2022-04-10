package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobsterbot;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        //Prints every server this bot is running on in the console

        Lobsterbot.LOGGER.info("Bot is running on:");

        for (var guild : event.getJDA().getGuilds()) {
            Lobsterbot.LOGGER.info("{} ({}) -- {}", guild.getName(), guild.getMemberCount(), guild.getIdLong());
        }

    }
}
