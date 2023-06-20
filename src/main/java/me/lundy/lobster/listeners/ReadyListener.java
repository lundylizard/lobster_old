package me.lundy.lobster.listeners;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        setCommandIds(event.getJDA());
    }

    private void setCommandIds(JDA jda) {
        jda.retrieveCommands().queue(this::updateCommandIds);
    }

    private void updateCommandIds(List<Command> commands) {
        Lobster lobster = Lobster.getInstance();
        CommandManager commandManager = lobster.getCommandManager();
        for (Command command : commands) {
            commandManager.getCommands().get(command.getName()).setId(command.getIdLong());
        }
    }
}
