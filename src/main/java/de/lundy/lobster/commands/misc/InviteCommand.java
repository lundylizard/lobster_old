package de.lundy.lobster.commands.misc;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.utils.ChatUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class InviteCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var permissions = new ArrayList<Permission>();
        permissions.add(Permission.MESSAGE_WRITE);          // Permission to send messages
        permissions.add(Permission.MESSAGE_EMBED_LINKS);    // Permission to embed links
        permissions.add(Permission.MESSAGE_HISTORY);        // Actually not sure if you need this - Permission to see messages from the past
        permissions.add(Permission.MESSAGE_READ);           // Permission to read / receive messages
        permissions.add(Permission.VIEW_CHANNEL);           // Permission to see the text / voice-channels
        permissions.add(Permission.VOICE_CONNECT);          // Permission to be able to connect to vc
        permissions.add(Permission.VOICE_SPEAK);            // Permission to send music to the vc / "to speak in vc"

        var inviteUrl = event.getJDA().getInviteUrl(permissions);

        event.getTextChannel().sendMessage(new EmbedBuilder()
                .setFooter(ChatUtils.randomFooter())
                .setDescription("**INVITE LOBSTER BOT**\n\n[Click here](" + inviteUrl + ") to invite this bot to your server.")
                .setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor())
                .build()).queue();

    }

}
