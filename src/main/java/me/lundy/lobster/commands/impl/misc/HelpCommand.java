package me.lundy.lobster.commands.impl.misc;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.commands.IgnoreChecks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Duration;
import java.util.Comparator;
import java.util.Map;

@IgnoreChecks
public class HelpCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("About lobster Bot", "https://github.com/lundylizard/lobster");
        embedBuilder.setDescription("Developed by lundylizard\n\n");
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());

        Map<String, BotCommand> commands = Lobster.getInstance().getCommandManager().getCommands();

        commands.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getKey().length()))
                .forEach(entry -> {
                    BotCommand command = entry.getValue();
                    String commandString = String.format("`/%s` - %s\n", command.name(), command.description());
                    embedBuilder.appendDescription(commandString);
                }
        );

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
