package me.lundy.lobster.commands.slash.misc;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Comparator;
import java.util.Map;

@CommandInfo(name = "help", description = "List of commands you can use")
public class HelpCommand extends Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("About lobster Bot", "https://github.com/lundylizard/lobster");
        embedBuilder.setDescription("Developed by lundylizard\n\n");
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        embedBuilder.setFooter(BotUtils.randomFooter());

        Map<String, Command> commands = Lobster.getInstance().getCommandManager().getCommands();
        commands.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -BotUtils.countLetters(entry.getKey())))
                .forEach(entry -> {
                    Command command = entry.getValue();
                    CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);
                    String commandString = String.format("</%s:%d> - %s\n", commandInfo.name(), command.getId(), commandInfo.description());
                    embedBuilder.appendDescription(commandString);
                }
        );

        Button discord = Button.link(Lobster.DISCORD_URL, "Discord");
        Button invite = Button.link(Lobster.INVITE_URL, "Invite");
        event.replyEmbeds(embedBuilder.build()).addActionRow(discord, invite).queue();
    }

}
