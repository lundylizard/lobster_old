package de.lundy.lobster.commands.admin;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public record AdminCommand(BlacklistManager blacklistManager,
                           SettingsManager settingsManager) implements Command {

    @Override
    public void action(String @NotNull [] args, @NotNull MessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() != 837077116545531974L) {
            return;
        }

        if (args.length != 0) {

            if (args[0].equalsIgnoreCase("blacklist"))
                if (args[1].equalsIgnoreCase("add")) {

                    var discordId = args[2];
                    var reason = new StringBuilder();

                    for (var i = 3; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }

                    try {

                        blacklistManager.putServerInBlacklistTable(Long.parseLong(discordId), reason.toString().trim());
                        event.getChannel().sendMessage(":white_check_mark: Added `" + discordId + "` to the blacklist with reason `" + reason.toString().trim() + "`").queue();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else if (args[1].equalsIgnoreCase("remove")) {

                    var discordId = args[2];

                    try {

                        blacklistManager.removeServerFromBlacklistTable(Long.parseLong(discordId));
                        event.getChannel().sendMessage(":white_check_mark: Removed `" + discordId + "` from the blacklist").queue();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

        }

    }

}

