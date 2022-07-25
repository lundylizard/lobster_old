package de.lundy.lobster.commands.impl;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    protected static final Map<String, Command> commands = new HashMap<>();

    /**
     * Executes action() from command from given command container if it was registered in commands Map on launch
     *
     * @param commandContainer Command container
     */
    public static void handleCommand(CommandContainer commandContainer) {

        if (commands.containsKey(commandContainer.invoke)) {
            commands.get(commandContainer.invoke).action(commandContainer.args, commandContainer.event);
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
