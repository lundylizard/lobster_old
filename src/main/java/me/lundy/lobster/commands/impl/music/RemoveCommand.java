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

public class RemoveCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        OptionMapping indexOption = event.getOption("index");
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            event.reply(":warning: The queue is currently empty.").setEphemeral(true).queue();
            return;
        }

        int humanIndex = indexOption.getAsInt();
        int queueIndex = humanIndex - 1;

        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);

        if (trackList.get(queueIndex) == null) {
            event.reply(":warning: This song is not in the queue.").setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.queue.removeFirstOccurrence(trackList.get(queueIndex));
        event.reply(String.format("Removed song `#%d` from the queue.", humanIndex)).queue();
    }

    @Override
    public String name() {
        return "remove";
    }

    @Override
    public String description() {
        return "Remove a song from the queue";
    }

    @Override
    public List<OptionData> options() {
        OptionData indexOption = new OptionData(OptionType.INTEGER, "index", "What song should be removed", true);
        return List.of(indexOption);
    }
}
