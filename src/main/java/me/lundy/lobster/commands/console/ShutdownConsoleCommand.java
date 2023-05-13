package me.lundy.lobster.commands.console;

import me.lundy.lobster.command.console.ConsoleCommand;
import me.lundy.lobster.command.console.ConsoleCommandInfo;
import net.dv8tion.jda.api.sharding.ShardManager;

@ConsoleCommandInfo(name = "shutdown", description = "Shut down lobster")
public class ShutdownConsoleCommand implements ConsoleCommand {

    @Override
    public void onCommand(String[] args, ShardManager shardManager) {
        shardManager.shutdown();
        System.out.println("Shutting down...");
        System.exit(1);
    }

}
