package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobster;
import de.lundy.lobster.commands.impl.CommandHandler;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class MessageCommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getChannelType() == ChannelType.PRIVATE) {
            return;
        }

        var message = event.getMessage();
        var serverId = event.getGuild().getId();

        String prefix;

        try {
            prefix = Lobster.getSettings().getPrefix(serverId);
        } catch (SQLException e) {
            event.getTextChannel().sendMessage("Could not find prefix for this server. Please re-invite the bot or contact lundylizard.").queue();
            return;
        }

        //If the message starts with the prefix and is not from a bot
        if (message.getContentRaw().startsWith(prefix) && !message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

            boolean blacklisted;

            try {
                blacklisted = Lobster.getBlacklist().getBlacklist().containsKey(Long.parseLong(serverId));
            } catch (SQLException e) {
                // Technically the error is known, but I don't want the user to know that a blacklist exists unless they are in it.
                event.getTextChannel().sendMessage("An unknown error occurred. Please try again later.").queue();
                return;
            }

            if (blacklisted) {

                String reason;

                try {
                    reason = Lobster.getBlacklist().getBlacklist().get(Long.parseLong(serverId));
                } catch (SQLException e) {
                    reason = "None";
                }

                event.getTextChannel().sendMessage(String.format(":warning: This server is blacklisted from using lobster. Reason: `%s`", reason)).queue();
                return;

            }

            CommandHandler.handleCommand(CommandHandler.parser.parse(message.getContentRaw(), event));

        }
    }
}
