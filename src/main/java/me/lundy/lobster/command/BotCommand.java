package me.lundy.lobster.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;

public abstract class BotCommand {

    private long id;

    public abstract void onCommand(CommandContext context);

    public abstract SlashCommandData getCommandData();

    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        return Collections.emptyList();
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
