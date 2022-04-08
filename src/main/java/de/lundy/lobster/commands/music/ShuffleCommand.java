package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class ShuffleCommand implements Command {

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

        var trackList = new LinkedList<>(queue);
        Collections.shuffle(trackList); //Shoutout to this
        queue.clear();
        queue.addAll(trackList);
        channel.sendMessage("Successfully shuffled the queue.").queue();

    }
}
