package de.lundy.lobster.commands.impl;

import de.lundy.lobster.utils.ChatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    public static final HashMap<String, Command> commands = new HashMap<>();

    /**
     * Executes action() from command from given command container if it was registered in commands Map on bootup
     *
     * @param cmd   Command container
     */
    public void handleCommand(@NotNull CommandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {
            commands.get(cmd.invoke).action(cmd.args, cmd.event);
            ChatUtils.print("COMMAND: (" + cmd.event.getGuild().getName() + ") " + cmd.event.getAuthor().getName() + "#" + cmd.event.getAuthor().getDiscriminator() + " executed command: [" + cmd.invoke + "] with args: " + Arrays.toString(cmd.args));
        }
    }

    /**
     * Puts class to the registered command map
     * @param invokes   Command invokes (with aliases)
     * @param command   Command class
     */
    public static void addCommand(String @NotNull [] invokes, Command command) {

        for (var aliases : invokes) {
            commands.put(aliases, command);
        }

    }
}
