package de.lundy.lobster.commands.admin;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

public record AdminCommand(BlacklistManager blacklistManager, SettingsManager settingsManager) implements Command {

    @Override
    public void action(String @NotNull [] args, @NotNull MessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() != 251430066775392266L) {
            return;
        }

        if (args.length == 0) {

            var serverCount = event.getJDA().getShardManager().getGuilds().size();
            var memberCount = event.getJDA().getShardManager().getGuilds().stream().mapToInt(Guild::getMemberCount).sum();
            var uptime = (System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getUptime()) / 1000;
            var connected = event.getJDA().getAudioManagers().stream().filter(AudioManager::isConnected).count();
            var shardInfo = event.getJDA().getShardInfo();

            String embedContent = String.format("""
                    **LOBSTER STATS**
                                
                    **Uptime:** <t:%d:R>
                    **Server Amount:** %d
                    **Member Count:** %d
                    **Shard Info:** %s
                    **Response Total:** %d
                    **Gateway Ping:** %d
                                
                    Connected to %d voice channel%s
                    """, uptime, serverCount, memberCount, shardInfo.getShardString(), event.getJDA().getResponseTotal(), event.getJDA().getGatewayPing(), connected, connected != 1 ? "s" : "");

            event.getTextChannel().sendMessage(new EmbedBuilder().setDescription(embedContent).build()).queue();

        } else {

            if (args[0].equalsIgnoreCase("blacklist")) {
                if (args[1].equalsIgnoreCase("add")) {

                    var reason = new StringBuilder();

                    for (var i = 3; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }

                    blacklistManager.putServerInBlacklistTable(Long.parseLong(args[2]), reason.toString().trim());
                    event.getChannel().sendMessage("Added `" + args[2] + "` to the blacklist. Reason: `" + reason.toString().trim() + "`").queue();
                    System.out.printf("Added %s to the blacklist. Reason: %s%n", args[2], reason.toString().trim());

                } else if (args[1].equalsIgnoreCase("remove")) {

                    blacklistManager.removeServerFromBlacklistTable(Long.parseLong(args[2]));
                    event.getChannel().sendMessage("Removed `" + args[2] + "` from the blacklist").queue();
                    System.out.printf("Removed %s from the blacklist%n", args[2]);

                }

            } else if (args[0].equalsIgnoreCase("reset")) {

                var discordId = Long.parseLong(args[1]);
                settingsManager.resetSettings(discordId);

            }
        }
    }
}

