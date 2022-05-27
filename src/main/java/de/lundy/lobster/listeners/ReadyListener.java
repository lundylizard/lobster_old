package de.lundy.lobster.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        //Prints every server this bot is running on in the console

        System.out.println("Bot is running on:");
        event.getJDA().getGuilds().stream().sorted(Comparator.comparingInt(Guild::getMemberCount)).forEach(g -> System.out.printf("» %s (%d) -- %d%n", g.getName(), g.getMemberCount(), g.getIdLong()));

    }
}
