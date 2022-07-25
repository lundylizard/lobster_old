package de.lundy.lobster.commands.misc;

import de.lundy.lobster.Lobster;
import de.lundy.lobster.commands.impl.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class PrefixCommand implements Command {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        // If the command is executed by a server admin
        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.ADMINISTRATOR)) {

            var serverId = event.getGuild().getIdLong();

            if (args.length == 0) {
                var currentPrefix = Lobster.getDatabase().getSettings().getPrefix(serverId);
                event.getChannel().asTextChannel().sendMessage("The current prefix on this server is `" + currentPrefix + "`").queue();
                return;
            }

            // If the prefix is longer than 10 characters
            if (args[0].length() >= 10) {
                event.getChannel().asTextChannel().sendMessage(":warning: Prefix is not allowed to be longer than 10 characters.").queue();
                return;
            }

            Lobster.getDatabase().getSettings().changePrefix(serverId, args[0]);
            event.getChannel().asTextChannel().sendMessage("Successfully set prefix for this server to `" + args[0] + "`").queue();

        } else {
            event.getChannel().asTextChannel().sendMessage(":warning: Only server administrators can change the prefix.").queue();
        }

    }
}
