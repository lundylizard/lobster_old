package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "remove", description = "Remove a song from the queue")
public class RemoveCommand extends Command implements CommandOptions {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(":warning: You are not in a voice channel").setEphemeral(true).queue();
            return;
        }

        if (!context.selfInVoice()) {
            context.getEvent().reply(":warning: I am not in a voice channel").setEphemeral(true).queue();
            return;
        }

        if (!context.inSameVoice()) {
            context.getEvent().reply(":warning: We are not in the same voice channel").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            context.getEvent().reply(":warning: The queue is currently empty").queue();
            return;
        }

        int index = context.getEvent().getOption("index").getAsInt();
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);

        if (trackList.get(index - 1) == null) {
            context.getEvent().reply(":warning: This song is not in the queue.").setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.queue.removeFirstOccurrence(trackList.get(index - 1));
        context.getEvent().replyFormat("Removed song `#%d` from the queue.", index).queue();
    }

    @Override
    public List<OptionData> options() {
        OptionData indexOption = new OptionData(OptionType.INTEGER, "index", "What song should be removed", true);
        return List.of(indexOption);
    }
}
