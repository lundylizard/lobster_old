package me.lundy.lobster.commands;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.settings.Settings;
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

    private final List<Command.Choice> ERROR_CHOICE = Collections.singletonList(new Command.Choice("An error occurred, please report this to lundy.", "-"));

    @Override
    public void onCommand(CommandContext context) {
        String setting = context.getEvent().getOption("setting").getAsString();
        String value = context.getEvent().getOption("value").getAsString();
        SettingsManager settingsManager = Lobster.getInstance().getSettingsManager();
        Settings settingSelected = Arrays.stream(Settings.values()).filter(s -> s.getName().equals(setting)).toList().get(0);
        try {
            settingsManager.updateSettingOfGuild(context.getGuild().getIdLong(), settingSelected, value);
        } catch (SQLException e) {
            context.getEvent().reply("Could not change setting").queue();
            e.printStackTrace();
            return;
        }
        context.getEvent().reply("Updated setting " + setting).setEphemeral(true).queue();
    }

    @Override
    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {

        String option = event.getFocusedOption().getName();
        List<Command.Choice> settings = new ArrayList<>();
        SettingsManager settingsManager = Lobster.getInstance().getSettingsManager();
        Map<String, Object> guildSettings;

        try {
            guildSettings = settingsManager.getSettingsFromGuild(event.getGuild().getIdLong());
        } catch (SQLException e) {
            return ERROR_CHOICE;
        }

        for (Settings setting : Settings.values()) {
            settings.add(new Command.Choice(setting.getFriendlyName(), setting.getName()));
        }

        if (option.equals("setting")) {
            return settings;
        }

        if (option.equals("value")) {
            Optional<OptionMapping> settingOption = Optional.ofNullable(event.getOption("setting"));
            if (settingOption.isPresent()) {
                Object requiredObject = guildSettings.get(settingOption.get().getAsString());
                return getChoicesFromObjectType(requiredObject);
            } else {
                return List.of(new Command.Choice("Please select a setting", "-"));
            }
        }

        return super.onAutocomplete(event);
    }

    private List<Command.Choice> getChoicesFromObjectType(Object o) {
        if (o instanceof String) return Collections.emptyList();
        if (o instanceof Long) return Collections.emptyList();
        List<Command.Choice> booleanChoices = new ArrayList<>();
        booleanChoices.add(new Command.Choice("On", "TRUE"));
        booleanChoices.add(new Command.Choice("Off", "FALSE"));
        if (o instanceof Boolean) return booleanChoices;
        return Collections.emptyList();
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSetting = new OptionData(OptionType.STRING, "setting", "Setting to change", true, true);
        OptionData optionValue = new OptionData(OptionType.STRING, "value", "New value of the setting", true, true);
        return Commands.slash("settings", "Change settings for this guild").addOptions(optionSetting, optionValue);
    }

}
