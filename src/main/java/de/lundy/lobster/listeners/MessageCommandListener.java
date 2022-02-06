package de.lundy.lobster.listeners;

import de.lundy.lobster.commands.impl.CommandHandler;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class MessageCommandListener extends ListenerAdapter {

    private final SettingsManager settingsManager;

    public MessageCommandListener(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        var message = event.getMessage();
        var commandHandler = new CommandHandler();

        try {
            //If the message starts with the prefix and is not from a bot
            if (message.getContentRaw().startsWith(settingsManager.getPrefix(event.getGuild().getIdLong())) &&
                    !message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                try {
                    commandHandler.handleCommand(CommandHandler.parser.parse(message.getContentRaw(), event, settingsManager));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
