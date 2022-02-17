package de.lundy.lobster.commands.admin;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import de.lundy.lobster.utils.mysql.SettingsManager;
import de.lundy.lobster.utils.mysql.StatsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;

public record AdminCommand(BlacklistManager blacklistManager,
                           SettingsManager settingsManager,
                           StatsManager statsManager) implements Command {

    @Override
    public void action(String @NotNull [] args, @NotNull MessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() != 251430066775392266L) {
            return;
        }

        if (args[0].equalsIgnoreCase("blacklist")) {
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


        } else if (args[0].equalsIgnoreCase("stats")) {

            var serverCount = event.getJDA().getGuilds().size();
            var memberCount = 0;
            var totalCommandsExecuted = 0;
            var totalTimePlayed = 0L;

            for (Guild guild : event.getJDA().getGuilds()) {
                for (Member ignored : guild.getMembers()) {
                    memberCount++;
                }
            }

            try {

                for (var serverCommand : statsManager.getTotalCommandsExecuted()) {
                    totalCommandsExecuted += serverCommand;
                }

                for (var time : statsManager.getTotalTimePlayed()) {
                    totalTimePlayed += time;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("**LOBSTER BOT STATS**\n\n" +
                            "serverCount = " + serverCount + "\n" +
                            "userCount = " + memberCount + "\n" +
                            "uptime = " + ManagementFactory.getRuntimeMXBean().getUptime() + "\n" +
                            "totalCommands = " + totalCommandsExecuted + "\n" +
                            "totalTimeVc = " + totalTimePlayed + "s")
                    .build()).queue();

        }

    }
}

