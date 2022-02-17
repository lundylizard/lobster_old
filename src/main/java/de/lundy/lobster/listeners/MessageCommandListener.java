package de.lundy.lobster.listeners;

import de.lundy.lobster.commands.impl.CommandHandler;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import de.lundy.lobster.utils.mysql.SettingsManager;
import de.lundy.lobster.utils.mysql.StatsManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class MessageCommandListener extends ListenerAdapter {

    private final SettingsManager settingsManager;
    private final BlacklistManager blacklistManager;
    private final StatsManager statsManager;

    public MessageCommandListener(SettingsManager settingsManager, BlacklistManager blacklistManager, StatsManager statsManager) {
        this.settingsManager = settingsManager;
        this.blacklistManager = blacklistManager;
        this.statsManager = statsManager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        var message = event.getMessage();
        var commandHandler = new CommandHandler();
        var serverId = event.getGuild().getIdLong();

        try {
            //If the message starts with the prefix and is not from a bot
            if (message.getContentRaw().startsWith(settingsManager.getPrefix(serverId)) &&
                    !message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                try {

                    if (blacklistManager.serverInBlacklistTable(serverId)) {
                        event.getTextChannel().sendMessage(":warning: This server is blacklisted from using lobster. Reason: `" + blacklistManager.getBlacklistReason(event.getGuild().getIdLong()) + "`").queue();
                        return;
                    }

                    commandHandler.handleCommand(CommandHandler.parser.parse(message.getContentRaw(), event, settingsManager));
                    statsManager.setCommandsExecuted(serverId, statsManager.getCommandsExecuted(serverId) + 1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
