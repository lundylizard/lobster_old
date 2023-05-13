package me.lundy.lobster.listeners;

import me.lundy.lobster.Lobster;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        List<Command> commands = event.getJDA().retrieveCommands().complete();
        commands.forEach(command -> {
            Lobster.getInstance().getCommandManager().getCommands().get(command.getName()).setId(command.getIdLong());
        });

    }
}
