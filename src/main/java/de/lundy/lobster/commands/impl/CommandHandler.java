package de.lundy.lobster.commands.impl;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    protected static final Map<String, Command> commands = new HashMap<>();

    /**
     * Executes action() from command from given command container if it was registered in commands Map on bootup
     *
     * @param cmd Command container
     */
    public void handleCommand(@NotNull CommandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {

            commands.get(cmd.invoke).action(cmd.args, cmd.event);

        }
    }

    /**
     * Puts class to the registered command map
     *
     * @param invokes Command invokes (with aliases)
     * @param command Command class
     */
    public static void addCommand(Command command, String... invokes) {

        for (var aliases : invokes) {
            commands.put(aliases, command);
        }

    }
}
