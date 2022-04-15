package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobster;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        //Prints every server this bot is running on in the console

        Lobster.LOGGER.info("Bot is running on:");

        for (var guild : event.getJDA().getGuilds()) {
            Lobster.LOGGER.info("{} ({}) -- {}", guild.getName(), guild.getMemberCount(), guild.getIdLong());
        }

    }
}
