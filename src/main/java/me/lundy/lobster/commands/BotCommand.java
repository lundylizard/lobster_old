package me.lundy.lobster.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public abstract class BotCommand {

    private final Logger logger = LoggerFactory.getLogger(name() + "-command");

    public abstract void onCommand(SlashCommandInteractionEvent event);

    public abstract String name();

    public abstract String description();

    public List<OptionData> options() {
        return Collections.emptyList();
    }

    public List<SubcommandData> subCommands() {
        return Collections.emptyList();
    }

    public Logger getLogger() {
        return this.logger;
    }

}
