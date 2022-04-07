package de.lundy.lobster.commands.impl;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandContainer {

    public final String raw;
    public final String beheaded;
    public final String invoke;
    public final String[] splitBeheaded;
    public final String[] args;
    public final MessageReceivedEvent event;

    /**
     * Container used to parse event to create command
     * @param raw           Raw content of command
     * @param beheaded      Raw content without prefix
     * @param splitBeheaded Beheaded content split by space
     * @param invoke        Command (first entry of array)
     * @param args          All entries of array except invoke
     * @param event         Event used for parsing
     */
    public CommandContainer(String raw, String beheaded, String[] splitBeheaded, String invoke, String[] args, MessageReceivedEvent event) {

        this.raw = raw;
        this.beheaded = beheaded;
        this.splitBeheaded = splitBeheaded;
        this.invoke = invoke;
        this.args = args;
        this.event = event;

    }
}
