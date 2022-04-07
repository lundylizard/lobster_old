package de.lundy.lobster.commands.misc;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record HelpCommand(SettingsManager settingsManager) implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var helpContent = "**LOBSTER MUSIC BOT**\n\n" +
                "Made by lundylizard » [Twitch](https://twitch.tv/Iundylizard) - [Twitter](https://twitter.com/lundylizard) - [GitHub](https://github.com/lundylizard/lobster)\n" +
                "Uptime: " + ChatUtils.getBotUptime() + " | Made in Java using [JDA](https://github.com/DV8FromTheWorld/JDA) & [LavaPlayer](https://github.com/sedmelluq/lavaplayer)\n\n" +
                "%prefix%join - Join the voice channel you're in.\n" +
                "%prefix%leave - Leave the voice channel.\n" +
                "%prefix%link - Sends link to the current song.\n" +
                "%prefix%move <pos1> <pos2> - Move songs in the queue.\n" +
                "%prefix%nowplaying - Displays the current song.\n" +
                "%prefix%play [top] - Adds a song to the queue [at the top].\n" +
                "%prefix%queue - Shows current queue.\n" +
                "%prefix%remove - Removes a song from the queue.\n" +
                "%prefix%seek <min:sec> - Changes position in the song.\n" +
                "%prefix%shuffle - Shuffles the queue.\n" +
                "%prefix%skip [amount] - Skips [amount] of songs in the queue.\n" +
                "%prefix%stop - Stops the playback and leaves vc.\n\n" +
                "__Misc. Commands:__\n" +
                "%prefix%prefix - Change the prefix for this bot.\n" +
                "%prefix%invite - Sends an bot invitation link into the channel.\n" +
                "%prefix%donate - Information about donating.\n";

        var serverId = event.getGuild().getIdLong();
        var prefix = settingsManager.getPrefix(serverId);

        event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor())
                .setDescription(helpContent.replace("%prefix%", prefix))
                .setFooter(ChatUtils.randomFooter())
                .setThumbnail("https://raw.githubusercontent.com/lundylizard/lobster/master/src/main/resources/t_lobster_image_PixelForgeGames.png")
                .build()).queue();

    }
}
