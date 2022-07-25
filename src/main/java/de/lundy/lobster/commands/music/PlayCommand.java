package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var link = new StringBuilder();
        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        assert selfVoiceState != null;
        if (!selfVoiceState.inAudioChannel()) {
            var audioManager = event.getGuild().getAudioManager();
            var memberChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
            audioManager.openAudioConnection(memberChannel);
            assert memberChannel != null;
            event.getChannel().sendMessage("Connecting to voice channel `\uD83D\uDD0A " + memberChannel.getName() + "`").queue();
        }

        // Put request video at the top if the first argument is "top"
        var top = args[0].equalsIgnoreCase("top");

        var attachments = event.getMessage().getAttachments();

        if (! attachments.isEmpty()) {

            if (attachments.size() > 1) {
                event.getTextChannel().sendMessage("Please request file embeds individually.").queue();
                return;
            }

            var att = attachments.get(0);
            PlayerManager.getInstance().loadAndPlay(event, att.getUrl(), top);
            return;
        }

        for (var i = top ? 1 : 0; i < args.length; i++) {
            link.append(args[i]).append(" ");
        }

        if (! isUrl(link.toString())) {
            link.insert(0, "ytsearch:");
        }

        if (link.isEmpty()) {
            channel.sendMessage(":warning: Please provide a file or an URL.").queue();
            return;
        }

        PlayerManager.getInstance().loadAndPlay(event, link.toString().trim(), top);

    }

    private boolean isUrl(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }
}