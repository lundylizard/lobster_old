package me.lundy.lobster.commands.console;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.console.ConsoleCommand;
import me.lundy.lobster.command.console.ConsoleCommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.awt.*;

@ConsoleCommandInfo(name = "announce", description = "Announce a message to all active text channels")
public class AnnounceConsoleCommand implements ConsoleCommand {

    @Override
    public void onCommand(String[] args, ShardManager shardManager) {

        StringBuilder message = new StringBuilder();

        for (String arg : args) {
            message.append(arg).append(" ");
        }

        String finalMessage = message.toString().replace("|", "\n");
        EmbedBuilder announcementEmbed = new EmbedBuilder();
        announcementEmbed.setColor(Color.RED);
        announcementEmbed.setDescription(finalMessage);
        announcementEmbed.setTitle("Announcement");

        Lobster.getInstance().getChannelCollector().getTextChannelIds().forEach((g, t) -> {
            shardManager.getGuildById(g).getTextChannelById(t).sendMessageEmbeds(announcementEmbed.build()).queue();
        });

    }

}
