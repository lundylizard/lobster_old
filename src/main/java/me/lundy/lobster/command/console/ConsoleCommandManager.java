package me.lundy.lobster.command.console;

import me.lundy.lobster.commands.console.*;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConsoleCommandManager {

    private final Map<String, ConsoleCommand> commands = new HashMap<>();

    public ConsoleCommandManager() {
        registerCommand(new AnnounceConsoleCommand());
        registerCommand(new HelpConsoleCommand());
        registerCommand(new ListConsoleCommand());
        registerCommand(new ShutdownConsoleCommand());
    }

    private void registerCommand(ConsoleCommand command) {
        ConsoleCommandInfo commandInfo = command.getClass().getAnnotation(ConsoleCommandInfo.class);
        this.commands.put(commandInfo.name().toLowerCase(), command);
    }

    public void handleCommand(String input, ShardManager shardManager) {
        String[] raw = input.split(" ");
        String invoke = raw[0];
        String[] args = Arrays.copyOfRange(raw, 1, raw.length);
        ConsoleCommand command = this.commands.get(invoke);
        if (command == null) return;
        command.onCommand(args, shardManager);
    }

    public Map<String, ConsoleCommand> getCommands() {
        return commands;
    }
}
