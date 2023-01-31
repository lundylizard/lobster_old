package me.lundy.lobster.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class ReadyListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(ReadyListener.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        logger.info("Bot is running on {} servers:", event.getJDA().getGuilds().size());
        event.getJDA().getGuilds().stream()
                .sorted(Comparator.comparingInt(Guild::getMemberCount))
                .forEach(g -> logger.info("» {} ({}) -- {}", g.getName(), g.getMemberCount(), g.getIdLong()));

    }
}
