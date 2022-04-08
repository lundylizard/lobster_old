package de.lundy.lobster.commands.admin;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class AdminCommand implements Command {

    private final BlacklistManager blacklistManager;

    public AdminCommand(BlacklistManager blacklistManager) {
        this.blacklistManager = blacklistManager;
    }

    @Override
    public void action(String @NotNull [] args, @NotNull MessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() != 251430066775392266L) {
            return;
        }

        if (args[0].equalsIgnoreCase("blacklist")) {
            if (args[1].equalsIgnoreCase("add")) {

                var reason = new StringBuilder();

                for (var i = 3; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }

                blacklistManager.putServerInBlacklistTable(Long.parseLong(args[2]), reason.toString().trim());
                event.getChannel().sendMessage("Added `" + args[2] + "` to the blacklist. Reason: `" + reason.toString().trim() + "`").queue();

            } else if (args[1].equalsIgnoreCase("remove")) {

                blacklistManager.removeServerFromBlacklistTable(Long.parseLong(args[2]));
                event.getChannel().sendMessage("Removed `" + args[2] + "` from the blacklist").queue();

            }
        }
    }
}

