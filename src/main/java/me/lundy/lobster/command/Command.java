package me.lundy.lobster.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command {

    private long id;
    private final Logger logger = LoggerFactory.getLogger(Command.class);

    public abstract void onCommand(SlashCommandInteractionEvent event);

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Logger getLogger() {
        return logger;
    }
}
