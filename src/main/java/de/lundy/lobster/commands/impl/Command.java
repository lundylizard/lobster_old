package de.lundy.lobster.commands.impl;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {

    /**
     * Specifies command output and logic
     * @param args      Arguments for the command
     * @param event     Event used in the command
     */
    void action(String[] args, MessageReceivedEvent event);

}
