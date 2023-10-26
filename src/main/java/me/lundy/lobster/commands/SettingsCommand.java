package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.database.settings.GuildSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class SettingsCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(context.getSelf().getColor());
        embedBuilder.setTitle("Guild Settings");
        embedBuilder.setDescription("Change the settings by using the buttons below\n\n");
        embedBuilder.appendDescription("`Keep Volume` - Whether the volume should reset when leaving VC\n");
        embedBuilder.appendDescription("`Collect Statistics` - Whether lobster should collect statistics of this guild");
        embedBuilder.appendDescription("`Update Notifications` - Whether lobster should send update notifications or not");
        Button settingTest = Button.success("changesetting:1", "Keep Volume");
        Button settingTest2 = Button.success("changesetting:2", "Collect Statistics");
        Button settingTest3 = Button.danger("changesetting:3", "Update Notifications");
        context.getEvent().replyEmbeds(embedBuilder.build()).addActionRow(settingTest, settingTest2, settingTest3).queue();

        //String settingToUpdate = context.getEvent().getOption("setting").getAsString();
        //String newValue = context.getEvent().getOption("value").getAsString();
        //SettingsManager settingsManager = new SettingsManager(Lobster.getInstance().getDatabase().getDataSource(), context.getGuild().getIdLong());
        //GuildSettings guildSettings;
//
        //try {
        //    guildSettings = settingsManager.getSettings(context.getGuild().getIdLong());
        //} catch (SQLException e) {
        //    context.getEvent().replyFormat("Could not get settings from this guild. %s", e.getMessage()).setEphemeral(true).queue();
        //    return;
        //}
//
        //updateSetting(guildSettings, settingToUpdate, newValue);
//
        //try {
        //    settingsManager.updateSettings(context.getGuild().getIdLong(), guildSettings);
        //} catch (SQLException e) {
        //    context.getEvent().reply("Could not change setting. " + e.getMessage()).queue();
        //    return;
        //}
//
        //context.getEvent().reply("Updated setting **" + settingToUpdate + "** to `" + newValue + "`").setEphemeral(true).queue();
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

        //String option = event.getFocusedOption().getName();
        //List<Command.Choice> settings = new ArrayList<>();
        //settings.add(new Command.Choice("Keep Volume", "keepVolume"));
        //settings.add(new Command.Choice("[Not Implemented Yet] Embed Color", "embedColor"));
        //settings.add(new Command.Choice("[Not Implemented Yet] Beta Features", "betaFeatures"));
        //settings.add(new Command.Choice("[Not Implemented Yet] Collect Statistics", "collectStatistics"));
        //settings.add(new Command.Choice("[Not Implemented Yet] Update Notifications", "updateNotifications"));
        //settings.add(new Command.Choice("Join VC automatically", ""));
//
        //if (option.equals("setting")) {
        //    return settings;
        //}
//
        //if (option.equals("value")) {
        //    Optional<OptionMapping> settingOption = Optional.ofNullable(event.getOption("setting"));
        //    if (settingOption.isEmpty()) {
        //        return List.of(new Command.Choice("Please select a setting first!", "-"));
        //    }
        //    if (settingOption.get().getAsString().equals("embedColor")) {
        //        settings = List.of(
        //                new Command.Choice("Role", "role"),
        //                new Command.Choice("#000000", "#000000")
        //        );
        //    } else {
        //        settings = List.of(
        //                new Command.Choice("No", "false"),
        //                new Command.Choice("Yes", "true")
        //        );
        //    }
        //    return settings;
        //}
//
        return super.onAutocomplete(event);
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSetting = new OptionData(OptionType.STRING, "setting", "Setting to change", true, true);
        OptionData optionValue = new OptionData(OptionType.STRING, "value", "New value of the setting", true, true);
        return Commands.slash("settings", "Change settings for this guild")/*.addOptions(optionSetting, optionValue)*/;
    }

}
