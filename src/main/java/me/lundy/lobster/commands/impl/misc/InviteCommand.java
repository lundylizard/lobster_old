package me.lundy.lobster.commands.impl.misc;

import me.lundy.lobster.Lobster;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.commands.IgnoreChecks;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@IgnoreChecks
public class InviteCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.reply("Press the button below to invite lobster to a server")
                .addActionRow(Button.link(Lobster.INVITE_URL, "Invite"))
                .queue();
    }

    @Override
    public String name() {
        return "invite";
    }

    @Override
    public String description() {
        return "Invite lobster to your server";
    }

}
