package me.lundy.lobster.commands;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.settings.GuildSettings;
import me.lundy.lobster.settings.SettingsManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.sql.SQLException;
import java.util.*;

public class SettingsCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        String settingToUpdate = context.getEvent().getOption("setting").getAsString();
        String newValue = context.getEvent().getOption("value").getAsString();
        SettingsManager settingsManager = Lobster.getInstance().getSettingsManager();
        GuildSettings guildSettings;
        try {
            guildSettings = settingsManager.getSettings(context.getGuild().getIdLong());
        } catch (SQLException e) {
            context.getEvent().replyFormat("Could not get settings from this guild. %s", e.getMessage()).setEphemeral(true).queue();
            return;
        }

        updateSetting(guildSettings, settingToUpdate, newValue);

        try {
            settingsManager.updateSettings(context.getGuild().getIdLong(), guildSettings);
        } catch (SQLException e) {
            context.getEvent().reply("Could not change setting. " + e.getMessage()).queue();
            return;
        }

        context.getEvent().reply("Updated setting **" + settingToUpdate + "** to `" + newValue + "`").setEphemeral(true).queue();
    }

    private void updateSetting(GuildSettings settings, String settingToUpdate, String newValue) {
        switch (settingToUpdate) {
            case "lastChannelUsedId":
                settings.setLastChannelUsedId(Long.parseLong(newValue));
                break;
            case "keepVolume":
                settings.setKeepVolume(Boolean.parseBoolean(newValue));
                break;
            case "embedColor":
                settings.setEmbedColor(newValue);
                break;
            case "betaFeatures":
                settings.setBetaFeatures(Boolean.parseBoolean(newValue));
                break;
            case "collectStatistics":
                settings.setCollectStatistics(Boolean.parseBoolean(newValue));
                break;
            case "updateNotifications":
                settings.setUpdateNotifications(Boolean.parseBoolean(newValue));
                break;
            default:
                break;
        }
    }


    @Override
    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {

        String option = event.getFocusedOption().getName();
        List<Command.Choice> settings = new ArrayList<>();
        settings.add(new Command.Choice("Keep Volume", "keepVolume"));
        settings.add(new Command.Choice("[Not Implemented Yet] Embed Color", "embedColor"));
        settings.add(new Command.Choice("[Not Implemented Yet] Beta Features", "betaFeatures"));
        settings.add(new Command.Choice("[Not Implemented Yet] Collect Statistics", "collectStatistics"));
        settings.add(new Command.Choice("[Not Implemented Yet] Update Notifications", "updateNotifications"));

        if (option.equals("setting")) {
            return settings;
        }

        if (option.equals("value")) {
            Optional<OptionMapping> settingOption = Optional.ofNullable(event.getOption("setting"));
            if (settingOption.isEmpty()) {
                return List.of(new Command.Choice("Please select a setting first!", "-"));
            }
            if (settingOption.get().getAsString().equals("embedColor")) {
                settings = List.of(
                        new Command.Choice("Role", "role"),
                        new Command.Choice("#000000", "#000000")
                );
            } else {
                settings = List.of(
                        new Command.Choice("No", "false"),
                        new Command.Choice("Yes", "true")
                );
            }
            return settings;
        }

        return super.onAutocomplete(event);
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSetting = new OptionData(OptionType.STRING, "setting", "Setting to change", true, true);
        OptionData optionValue = new OptionData(OptionType.STRING, "value", "New value of the setting", true, true);
        return Commands.slash("settings", "Change settings for this guild").addOptions(optionSetting, optionValue);
    }

}
