package me.lundy.lobster.command.console;

import net.dv8tion.jda.api.sharding.ShardManager;

public interface ConsoleCommand {
    void onCommand(String[] args, ShardManager shardManager);
}
