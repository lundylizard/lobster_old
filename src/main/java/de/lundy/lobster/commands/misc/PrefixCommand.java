package de.lundy.lobster.commands.misc;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public record PrefixCommand(SettingsManager settingsManager) implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var serverId = event.getGuild().getIdLong();

        try {

            //If the command is executed by a server admin
            if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.ADMINISTRATOR)) {

                if (args.length == 0) {

                    var currentPrefix = settingsManager.getPrefix(serverId);
                    event.getTextChannel().sendMessage(":information_source: The current prefix on this server is `" + currentPrefix + "`").queue();

                } else if (args.length == 1) {

                    if (args[0].length() <= 10) { // Why would you even do this?

                        settingsManager.setPrefix(serverId, args[0]);
                        event.getTextChannel().sendMessage(":white_check_mark: Successfully set prefix for this server to `" + args[0] + "`").queue();

                    } else {
                        event.getTextChannel().sendMessage(":warning: Prefix is not allowed to be longer than 10 characters.").queue();
                    }
                }

            } else {
                event.getTextChannel().sendMessage(":warning: Only server administrators can change the prefix.").queue();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
