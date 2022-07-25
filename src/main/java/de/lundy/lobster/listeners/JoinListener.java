package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobster;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {

        var guildJoined = event.getGuild();
        System.out.printf("Joined Discord Server %s (%d) -- %s%n", guildJoined.getName(), guildJoined.getMemberCount(), guildJoined.getId());

        // Create guild tables on join
        if (!Lobster.getDatabase().getSettings().getRegisteredServers().contains(guildJoined.getIdLong())) {

            System.out.printf("%s is not in the database yet. Registering...%n", guildJoined.getName());
            Lobster.getDatabase().getSettings().registerServer(guildJoined.getIdLong());
            System.out.printf("Successfully registered %s%n", guildJoined.getName());

        }

    }
}
