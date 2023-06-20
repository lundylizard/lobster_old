package me.lundy.lobster.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command {

    private long id;
    private final Logger logger = LoggerFactory.getLogger(Command.class);

    public abstract void onCommand(CommandContext context);

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
