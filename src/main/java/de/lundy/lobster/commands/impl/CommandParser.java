package de.lundy.lobster.commands.impl;

import de.lundy.lobster.Lobster;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class CommandParser {

    /**
     * Parses the event and raw input to a command container
     *
     * @param raw   Raw content from event
     * @param event event
     *
     * @return Command container from parsed raw string and event
     */
    public CommandContainer parse(String raw, MessageReceivedEvent event) {

        String prefix;

        try {
            prefix = Lobster.getDatabase().getSettings().getPrefix(event.getGuild().getId());
        } catch (SQLException e) {
            return null;
        }

        var beheaded = raw.replace(prefix, "");
        var splitBeheaded = beheaded.split(" ");
        var invoke = splitBeheaded[0].toLowerCase();
        var split = new ArrayList<String>();
        Collections.addAll(split, splitBeheaded);
        var args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);
        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, event);

    }

}
