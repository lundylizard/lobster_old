package me.lundy.lobster.listeners.buttons;

import com.zaxxer.hikari.HikariDataSource;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.database.settings.CommandHistoryManager;
import me.lundy.lobster.database.settings.GuildSettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DataButtonListener extends ListenerAdapter {

    private static final HikariDataSource dataSource = Lobster.getInstance().getDatabase().getDataSource();
    private static final GuildSettingsManager GUILD_SETTINGS = new GuildSettingsManager(dataSource);
    private static final CommandHistoryManager COMMAND_HISTORY = new CommandHistoryManager(dataSource);

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("data")) {
            long guildId = event.getGuild().getIdLong();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Data");
            embedBuilder.setDescription("lastChannelUsed = <#" + GUILD_SETTINGS.getLastChannelUsedId(guildId) + ">\n");
            embedBuilder.appendDescription("commandHistory(size) = `" + COMMAND_HISTORY.getCommandHistoryFromGuild(guildId).size() + "`");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }
}
