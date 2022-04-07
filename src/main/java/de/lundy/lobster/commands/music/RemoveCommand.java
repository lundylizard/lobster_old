package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.ChatUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class RemoveCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var queue = musicManager.scheduler.queue;
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();
        var selfVoiceState = self.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        assert selfVoiceState != null;
        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        if (queue.isEmpty()) {
            channel.sendMessage(":warning: The queue is currently empty.").queue();
            return;
        }

        if (!ChatUtils.checkIfValidNumber(args[0])) {
            channel.sendMessage(":warning: `" + args[0] + "` is not a valid value.").queue();
            return;
        }

        var index = Integer.parseInt(args[0]);

        if (index < 1 && index < queue.size()) {
            channel.sendMessage(":warning: Track `#" + args[0] + "` is not in the queue.").queue();
            return;
        }

        var trackList = new ArrayList<>(queue);
        queue.removeFirstOccurrence(trackList.get(index - 1));
        event.getChannel().sendMessage(":white_check_mark: Successfully removed track `#" + index + "`").queue();

    }

}
