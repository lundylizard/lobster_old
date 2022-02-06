package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public record HelpCommand(SettingsManager settingsManager) implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var helpContent = "**LOBSTER MUSIC BOT**\n\n" +
                "Made by lundylizard » [Twitch](https://twitch.tv/Iundylizard) - [Twitter](https://twitter.com/lundylizard) - [GitHub](?)\n" +
                "Uptime: " + ChatUtils.getBotUptime() + " | Made in Java using [JDA](https://github.com/DV8FromTheWorld/JDA) & [LavaPlayer](https://github.com/sedmelluq/lavaplayer)\n\n" +
                "%prefix%join - Join the voice channel.\n" +
                "%prefix%leave - Leave the voice channel. `(Alias: %prefix%dc, %prefix%disconnect)`\n" +
                "%prefix%link - Sends link to the current song. `(Alias: %prefix%url)`\n" +
                "%prefix%move <pos1> <pos2> - Move songs in the queue.\n" +
                "%prefix%nowplaying - Shows current song. `(Alias: %prefix%np)`\n" +
                "%prefix%play - Plays a song. `(Alias: %prefix%p, %prefix%sr)`\n" +
                "%prefix%queue - Shows current queue. `(Alias: %prefix%q)`\n" +
                "%prefix%remove - Removes a song from the queue. `(Alias: %prefix%rm)`\n" +
                "%prefix%seek <min:sec> - Changes position in the song. `(Alias: %prefix%se)`\n" +
                "%prefix%shuffle - Shuffles the queue. `(Alias: %prefix%sh)`\n" +
                "%prefix%skip [amount] - Skips [amount] of songs in the queue. `(Alias: %prefix%s)`\n" +
                "%prefix%stop - Stops the playback.\n" +
                "%prefix%prefix - Change the prefix for this bot.\n";

        var serverId = event.getGuild().getIdLong();
        String prefix = "";

        try {
            prefix = settingsManager.getPrefix(serverId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor())
                .setDescription(helpContent.replace("%prefix%", prefix))
                .build()).queue();

    }
}
