package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class RemoveCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
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

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            channel.sendMessage(":warning: The queue is currently empty.").queue();
            return;
        }

        int index = 0;
        if (! BotUtils.isRange(args[0])) {
            try {
                index = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
        }

        if (args[0].contains("-") && BotUtils.isRange(args[0])) {

            var indexRange = new int[]{Integer.parseInt(args[0].split("-")[0]), Integer.parseInt(args[0].split("-")[1])};

            if (! (BotUtils.isValidIndex(indexRange[0]) && BotUtils.isValidIndex(indexRange[1]))) {
                event.getTextChannel().sendMessage(":warning: Invalid range").queue();
                return;
            }

            var highest = Math.max(indexRange[0], indexRange[1]);
            var lowest = Math.min(indexRange[0], indexRange[1]);

            if (queue.size() < highest) {
                event.getTextChannel().sendMessage(":warning: Provided range is too high. Not enough songs in queue.").queue();
                return;
            }

            var trackList = new ArrayList<>(queue);
            trackList.subList(lowest - 1, highest).clear();
            queue.clear();
            queue.addAll(trackList);
            event.getTextChannel().sendMessage("Removed tracks " + lowest + "-" + highest + " from the queue.").queue();

        } else if (BotUtils.isValidIndex(Integer.parseInt(args[0]))) {

            var trackList = new ArrayList<>(queue);
            queue.removeFirstOccurrence(trackList.get(index - 1));
            event.getChannel().sendMessage("Successfully removed track `#" + index + "`").queue();

        } else {
            event.getTextChannel().sendMessage(":warning: Invalid argument: Please use `remove [index] | [from]-[to]`").queue();
        }

    }

}
