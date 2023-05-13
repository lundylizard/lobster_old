package me.lundy.lobster.command;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface CommandOptions {
    List<OptionData> options();
}
