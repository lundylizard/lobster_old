package me.lundy.lobster.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, Command> commands;

    public CommandManager() {
        this.commands = new HashMap<>();
        findCommands().forEach(this::registerCommand);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = this.commands.get(event.getName());

        if (command == null) {
            event.reply(":warning: Unexpected error: `Command is null`").setEphemeral(true).queue();
            return;
        }

        CommandContext commandContext = new CommandContext(event);
        command.onCommand(commandContext);
    }

    private void registerCommand(Command command) {
        CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);

        if (commandInfo == null) {
            throw new IllegalArgumentException(command.getClass().getName() + " is missing CommandInfo annotation");
        }

        commands.putIfAbsent(commandInfo.name(), command);
    }

    public Map<String, Command> getCommands() {
        return this.commands;
    }

    public List<SlashCommandData> getCommandDataList() {
        return this.commands.values().stream().map(this::createCommandData).collect(Collectors.toList());
    }

    private SlashCommandData createCommandData(Command command) {
        CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);

        if (commandInfo == null) {
            throw new IllegalArgumentException(command.getClass().getName() + " is missing CommandInfo annotation");
        }

        return Commands.slash(commandInfo.name(), commandInfo.description())
                .setGuildOnly(true)
                .addOptions(command instanceof CommandOptions ? ((CommandOptions) command).options() : Collections.emptyList());
    }

    private List<Command> findCommands() {
        List<Command> commands = new ArrayList<>();

        try {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = "me/lundy/lobster/commands";
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {

                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {

                    File[] files = directory.listFiles();

                    if (files != null) {

                        Arrays.stream(files).filter(file -> file.isFile() && file.getName().endsWith(".class"))
                                .map(file -> "me.lundy.lobster.commands" + '.' + file.getName().substring(0, file.getName().length() - 6))
                                .map(className -> {

                                    try {
                                        Class<?> clazz = Class.forName(className);
                                        return clazz.getDeclaredConstructor().newInstance();
                                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                                             NoSuchMethodException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }

                                    return null;
                                }).filter(Command.class::isInstance).map(Command.class::cast).forEach(commands::add);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return commands;
    }
}
