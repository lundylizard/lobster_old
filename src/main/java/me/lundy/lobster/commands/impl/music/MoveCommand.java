package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class MoveCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        int from = event.getOption("from").getAsInt();
        int to = event.getOption("to").getAsInt();

        if (from <= 0 || to <= 0) {
            event.reply("The new index has to be higher than 0").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        from = Math.min(from, musicManager.scheduler.queue.size());
        to = Math.max(to, musicManager.scheduler.queue.size());

        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        trackList.add(to - 1, trackList.get(from - 1));
        trackList.remove(from);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);

        event.reply(String.format("Successfully moved track `#%d` to `#%d`", from, to)).queue();

    }

    @Override
    public String name() {
        return "move";
    }

    @Override
    public String description() {
        return "Move songs in the queue";
    }

    @Override
    public List<OptionData> options() {
        OptionData optionDataFrom = new OptionData(OptionType.INTEGER, "from", "What song you want to move", true);
        OptionData optionDataTo = new OptionData(OptionType.INTEGER, "to", "Where the song should be moved to", true);
        return List.of(optionDataFrom, optionDataTo);
    }

}
