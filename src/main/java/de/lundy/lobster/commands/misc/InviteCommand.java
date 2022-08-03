package de.lundy.lobster.commands.misc;

import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class InviteCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) {
            return;
        }

        if (event.getName().equalsIgnoreCase("invite")) {

            var permissions = new ArrayList<Permission>();
            permissions.add(Permission.MESSAGE_SEND);           // Permission to send messages
            permissions.add(Permission.MESSAGE_EMBED_LINKS);    // Permission to embed links
            permissions.add(Permission.VIEW_CHANNEL);           // See text/voice-channels
            permissions.add(Permission.VOICE_CONNECT);          // Connect to VC
            permissions.add(Permission.VOICE_SPEAK);            // Send music
            permissions.add(Permission.MANAGE_CHANNEL);         // Modify voice channel size
            permissions.add(Permission.VOICE_MOVE_OTHERS);      // Join full channel

            var inviteUrl = event.getJDA().getInviteUrl(permissions);

            event.replyEmbeds(new EmbedBuilder().setFooter(BotUtils.randomFooter()).setDescription("**INVITE LOBSTER BOT**\n\n[Click here](" + inviteUrl + ") to invite this bot to your server.").setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor()).build()).queue();

        }

    }

}
