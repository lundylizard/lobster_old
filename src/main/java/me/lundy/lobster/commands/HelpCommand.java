package me.lundy.lobster.commands;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Comparator;
import java.util.Map;

public class HelpCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("About lobster Bot", "https://github.com/lundylizard/lobster");
        embedBuilder.setDescription("Developed by lundylizard\n\n");
        embedBuilder.setColor(context.getGuild().getSelfMember().getColor());

        Map<String, BotCommand> commands = Lobster.getInstance().getCommandManager().getCommands();
        commands.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -StringUtils.countLetters(entry.getKey())))
                .forEach(entry -> {
                    BotCommand command = entry.getValue();
                    String commandString = String.format("</%s:%d> - %s\n", command.getCommandData().getName(), command.getId(), command.getCommandData().getDescription());
                    embedBuilder.appendDescription(commandString);
                }
        );

        Button discord = Button.link(Lobster.DISCORD_URL, "Discord");
        Button invite = Button.link(Lobster.INVITE_URL, "Invite");
        context.getEvent().replyEmbeds(embedBuilder.build()).addActionRow(discord, invite).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("help", "List of commands you can use");
    }

}
