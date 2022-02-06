package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.ChatUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

public class MoveCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        assert selfVoiceState != null;
        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: I am not playing anything.").queue();
            return;
        }

        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        if (args.length == 2) {

            var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            var queue = musicManager.scheduler.queue;

            if (ChatUtils.checkIfNumber(args[0])) {
                event.getTextChannel().sendMessage(":x: " + args[0] + " is not a valid number.").queue();
                return;
            }

            if (ChatUtils.checkIfNumber(args[1])) {
                event.getTextChannel().sendMessage(":x: " + args[1] + " is not a valid number.").queue();
                return;
            }

            var currentTrackPos = Integer.parseInt(args[0]);
            var movedTrackPos = Integer.parseInt(args[1]);
            var trackList = new LinkedList<>(queue);
            trackList.add(movedTrackPos - 1, trackList.get(currentTrackPos - 1).makeClone());
            trackList.remove(currentTrackPos + 1);
            queue.clear();
            queue.addAll(trackList);
            channel.sendMessage(":white_check_mark: Moved track `#" + args[0] + "` to position `#" + args[1] + "`").queue();

        } else {
            event.getTextChannel().sendMessage(":x: Invalid arguments, please use `move [pos1] [pos2]`").queue();
        }

    }
}
