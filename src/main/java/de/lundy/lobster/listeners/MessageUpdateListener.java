package de.lundy.lobster.listeners;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageUpdateListener extends ListenerAdapter {

    // private final SettingsManager settingsManager;
    // private final BlacklistManager blacklistManager;

    // public MessageUpdateListener(SettingsManager settingsManager, BlacklistManager blacklistManager) {
    //     this.settingsManager = settingsManager;
    //     this.blacklistManager = blacklistManager;
    // }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {

        // var message = event.getMessage();
        // var commandHandler = new CommandHandler();
        // var serverId = event.getGuild().getIdLong();

        //If the message starts with the prefix and is not from a bot
        // if (message.getContentRaw().startsWith(settingsManager.getPrefix(serverId)) && ! message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

        // if (blacklistManager.serverInBlacklistTable(serverId)) {
        //     event.getTextChannel().sendMessage(":warning: This server is blacklisted from using lobster. Reason: `" + blacklistManager.getBlacklistReason(event.getGuild().getIdLong()) + "`").queue();
        //     return;
        // }

        // commandHandler.handleCommand(CommandHandler.parser.parse(message.getContentRaw(), null, settingsManager));

        // }
    }

}
