package me.lundy.lobster.commands;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class HelpCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("About lobster");
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        embedBuilder.setDescription("Developed by [lundylizard](discord://discord.com/users/251430066775392266)\n\n");

        var commands = Lobster.getCommandManager().getCommands();
        commands.forEach((name, command) -> {
            var description = command.getCommandData().getDescription();
            var commandString = String.format("`/%s` - %s\n", name, description);
            embedBuilder.appendDescription(commandString);
        });

        var inviteUrl = event.getJDA().getInviteUrl(Permission.getPermissions(2150647808L));
        var discord = Button.link(Lobster.DISCORD_URL, "Discord");
        var invite = Button.link(inviteUrl, "Invite");
        event.replyEmbeds(embedBuilder.build()).addActionRow(discord, invite).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("help", "List of commands you can use");
    }

}
