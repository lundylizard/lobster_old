package me.lundy.lobster.commands;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Comparator;
import java.util.Map;

@CommandInfo(name = "help", description = "List of commands you can use")
public class HelpCommand extends Command {

    @Override
    public void onCommand(CommandContext context) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("About lobster Bot", "https://github.com/lundylizard/lobster");
        embedBuilder.setDescription("Developed by lundylizard\n\n");
        embedBuilder.setColor(context.getGuild().getSelfMember().getColor());

        Map<String, Command> commands = Lobster.getInstance().getCommandManager().getCommands();
        commands.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -StringUtils.countLetters(entry.getKey())))
                .forEach(entry -> {
                    Command command = entry.getValue();
                    CommandInfo commandInfo = command.getClass().getAnnotation(CommandInfo.class);
                    String commandString = String.format("</%s:%d> - %s\n", commandInfo.name(), command.getId(), commandInfo.description());
                    embedBuilder.appendDescription(commandString);
                }
        );

        Button discord = Button.link(Lobster.DISCORD_URL, "Discord");
        Button invite = Button.link(Lobster.INVITE_URL, "Invite");
        context.getEvent().replyEmbeds(embedBuilder.build()).addActionRow(discord, invite).queue();
    }

}
