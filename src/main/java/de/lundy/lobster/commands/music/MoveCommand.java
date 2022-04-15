package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MoveCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        if (!(selfVoiceState != null && selfVoiceState.inVoiceChannel())) {
            channel.sendMessage(":warning: I am not playing anything.").queue();
            return;
        }

        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        if (!(memberVoiceState != null && memberVoiceState.inVoiceChannel())) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        if (args.length == 2) {

            int[] swap = new int[2];

            try {
                swap = BotUtils.parseAsInt(args);
            } catch (NumberFormatException e) {
                event.getTextChannel().sendMessage(":warning: `" + args[0] + "` and `" + args[1] + "` are not valid.").queue();
            }

            var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            var queue = musicManager.scheduler.queue;
            var from = -- swap[0];
            var to = -- swap[1];

            if (from > queue.size()) {
                event.getTextChannel().sendMessage(":warning: Track `#" + ++ from + "` is not in the queue.").queue();
                return;
            }

            var trackList = new ArrayList<>(queue);
            trackList.add(to, trackList.get(from));
            trackList.remove(from + 1);
            queue.clear();
            queue.addAll(trackList);

            channel.sendMessage("Moved track `#" + ++ from + "` to position `#" + ++ to + "`").queue();

        } else {
            event.getTextChannel().sendMessage(":warning: Invalid arguments, please use `move [from] [to]`").queue();
        }

    }
}
