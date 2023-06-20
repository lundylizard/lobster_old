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

@CommandInfo(name = "move", description = "Move songs in the queue")
public class MoveCommand extends Command implements CommandOptions {

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

        int from = context.getEvent().getOption("from").getAsInt(); // No null check because required
        int to = context.getEvent().getOption("to").getAsInt(); // No null check because required

        if (from <= 0 || to <= 0) {
            context.getEvent().reply("The new position has to be higher than 0").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        from = Math.min(from, musicManager.scheduler.queue.size());
        to = Math.min(to, musicManager.scheduler.queue.size());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        trackList.add(to - 1, trackList.get(from - 1));
        trackList.remove(from);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);
        context.getEvent().reply(String.format("Successfully moved track `#%d` to `#%d`", from, to)).queue();
    }

    @Override
    public List<OptionData> options() {
        OptionData optionDataFrom = new OptionData(OptionType.INTEGER, "from", "What song you want to move", true);
        OptionData optionDataTo = new OptionData(OptionType.INTEGER, "to", "Where the song should be moved to", true);
        return List.of(optionDataFrom, optionDataTo);
    }

}
