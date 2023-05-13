package me.lundy.lobster.commands.slash.misc;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.IgnoreChecks;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@CommandInfo(name = "invite", description = "Invite lobster to your server")
public class InviteCommand extends Command {

    @Override
    @IgnoreChecks
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("Press the button below to invite lobster to a server")
                .addActionRow(Button.link(Lobster.INVITE_URL, "Invite"))
                .queue();
    }

}
