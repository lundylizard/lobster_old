package de.lundy.lobster.commands.misc;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class InviteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) {
            return;
        }

        if (event.getName().equalsIgnoreCase("invite")) {

            String inviteUrl = "https://discord.com/api/oauth2/authorize?client_id=891760327522394183&permissions=2150647808&scope=bot%20applications.commands";
            event.reply("Click the button below to add lobster to your server:").addActionRow(Button.link(inviteUrl, "Invite")).queue();

        }

    }

}
