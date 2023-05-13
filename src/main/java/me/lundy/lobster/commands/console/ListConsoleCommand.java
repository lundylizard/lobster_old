package me.lundy.lobster.commands.console;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.console.ConsoleCommand;
import me.lundy.lobster.command.console.ConsoleCommandInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

@ConsoleCommandInfo(name = "list", description = "List a variety of information of the bot")
public class ListConsoleCommand implements ConsoleCommand {

    @Override
    public void onCommand(String[] args, ShardManager shardManager) {

        if (args.length == 0) {
            System.out.printf("Lobster is in %d servers.%n", shardManager.getGuilds().size());
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("active")) {

                List<Guild> guilds = shardManager.getGuilds().stream().filter(g -> g.getAudioManager().isConnected()).toList();

                if (guilds.isEmpty()) {
                    System.out.println("No guilds are active right now.");
                } else {
                    System.out.printf("lobster is active in %d guilds right now.", guilds.size());
                }

            } else if (args[0].equalsIgnoreCase("tc")) {

                System.out.println(Lobster.getInstance().getChannelCollector().getTextChannelIds().size());
                Lobster.getInstance().getChannelCollector().getTextChannelIds().forEach((g, t) -> {
                    System.out.printf("%s -- %s%n", shardManager.getGuildById(g).getName(), shardManager.getGuildById(g).getTextChannelById(t).getName());
                });

            } else if (args[0].equalsIgnoreCase("commands")) {
                shardManager.getShards().get(0).retrieveCommands().queue(commands -> {
                    commands.forEach(command -> {
                        System.out.println(command.getName() + " -- " + command.getId());
                    });
                });

            }
        }
    }

}
