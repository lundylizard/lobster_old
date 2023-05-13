package me.lundy.lobster.commands.console;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.console.ConsoleCommand;
import me.lundy.lobster.command.console.ConsoleCommandInfo;
import net.dv8tion.jda.api.sharding.ShardManager;

@ConsoleCommandInfo(name = "help", description = "Get a list of all console commands")
public class HelpConsoleCommand implements ConsoleCommand {

    @Override
    public void onCommand(String[] args, ShardManager shardManager) {

        StringBuilder output = new StringBuilder();
        Lobster.getInstance().getConsoleCommandManager().getCommands().forEach((invoke, command) -> {
            ConsoleCommandInfo commandInfo = command.getClass().getAnnotation(ConsoleCommandInfo.class);
            output.append(commandInfo.name()).append(" ").append(commandInfo.description());
        });

        System.out.println(output);

    }

}
