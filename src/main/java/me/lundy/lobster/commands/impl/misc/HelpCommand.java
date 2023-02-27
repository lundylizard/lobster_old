package me.lundy.lobster.commands.impl.misc;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.commands.IgnoreChecks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@IgnoreChecks
public class HelpCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder description = new StringBuilder();

        embedBuilder.setTitle("About lobster Bot", "https://github.com/lundylizard/lobster");
        description.append("Developed by lundylizard\n\n");

        List<Command> allCommands = event.getJDA().retrieveCommands().complete();

        allCommands.stream().sorted(
                Comparator.comparingInt(c -> -c.getName().length())
        ).forEach(command -> {
            description.append(String.format("`/%s` - %s\n", command.getName(), command.getDescription()));
        });

        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        embedBuilder.setDescription(description.toString());
        event.replyEmbeds(embedBuilder.build()).addActionRow(
                Button.link(Lobster.DISCORD_URL, "Discord"),
                Button.link(Lobster.INVITE_URL, "Invite")
        ).queue();

    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "List of commands you can use";
    }

}
