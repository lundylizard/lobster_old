package de.lundy.lobster.listeners;

import de.lundy.lobster.commands.impl.CommandHandler;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageCommandListener extends ListenerAdapter {

    private final SettingsManager settingsManager;
    private final BlacklistManager blacklistManager;

    public MessageCommandListener(SettingsManager settingsManager, BlacklistManager blacklistManager) {
        this.settingsManager = settingsManager;
        this.blacklistManager = blacklistManager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        var message = event.getMessage();
        var commandHandler = new CommandHandler();
        var serverId = event.getGuild().getIdLong();

        // Avoid private messages
        if (event.getChannelType() == ChannelType.PRIVATE) {
            return;
        }

        //If the message starts with the prefix and is not from a bot
        if (message.getContentRaw().startsWith(settingsManager.getPrefix(serverId)) &&
                !message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

            if (blacklistManager.serverInBlacklistTable(serverId)) {
                event.getTextChannel().sendMessage(":warning: This server is blacklisted from using lobster. Reason: `" + blacklistManager.getBlacklistReason(event.getGuild().getIdLong()) + "`").queue();
                return;
            }

            commandHandler.handleCommand(CommandHandler.parser.parse(message.getContentRaw(), event, settingsManager));

        }
    }
}
