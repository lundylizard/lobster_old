package de.lundy.lobster.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        //Prints every server this bot is running on in the console

        System.out.println("Bot is running on:");

        for (var guild : event.getJDA().getGuilds()) {
            System.out.println(guild.getName() + " (" + guild.getMembers().size() + ") -- ID: " + guild.getId());
        }

    }

}
