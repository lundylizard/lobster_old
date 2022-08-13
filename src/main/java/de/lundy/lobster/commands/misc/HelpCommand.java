package de.lundy.lobster.commands.misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class HelpCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("help")) {

            EmbedBuilder embedBuilder = new EmbedBuilder();
            StringBuilder description = new StringBuilder();
            embedBuilder.setTitle("About lobster Bot", "https://github.com/lundylizard/lobster");
            description.append("lobster Bot (v2.1.1)\n");
            description.append("Developed by lundylizard\n\n");
            event.getJDA().retrieveCommands().complete().stream().sorted(Comparator.comparingInt(c -> -c.getName().length())).forEach(command -> description.append("`/").append(command.getName()).append("` - ").append(command.getDescription()).append("\n"));
            embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
            embedBuilder.setDescription(description.toString());
            event.replyEmbeds(embedBuilder.build()).addActionRow(Button.link("https://ko-fi.com/lundylizard", "Ko-Fi"), Button.link("https://github.com/lundylizard/lobster", "GitHub"), Button.link("https://discord.gg/Hk5YP5AWhW", "Discord"), Button.link("https://twitter.com/lundylizard", "Twitter"), Button.link("https://twitch.tv/iundylizard", "Twitch")).queue();

        }

    }
}
