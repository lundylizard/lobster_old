package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

public class PrefixCommand implements Command {

    private final SettingsManager settingsManager;

    public PrefixCommand(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var serverId = event.getGuild().getIdLong();

        try {

            //If the command is executed by a server admin
            if (Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.ADMINISTRATOR)) {

                if (args.length == 0) {

                    var currentPrefix = settingsManager.getPrefix(serverId);
                    event.getTextChannel().sendMessage("» Prefix on this server is `" + currentPrefix + "`").queue();

                } else if (args.length == 1) {

                    if (args[0].length() > 10) {

                        settingsManager.setPrefix(serverId, args[0]);
                        event.getTextChannel().sendMessage("Successfully set prefix for this server to `" + args[0] + "`").queue();

                    } else {
                        event.getTextChannel().sendMessage("Prefix is not allowed to be longer than 10 characters.").queue();
                    }
                }

            } else {
                event.getTextChannel().sendMessage("Only administrators can change the prefix.").queue();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
