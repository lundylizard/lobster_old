package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        Collections.shuffle(trackList);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);
        event.reply("Shuffled the queue.").queue();
    }

    @Override
    public String name() {
        return "shuffle";
    }

    @Override
    public String description() {
        return "Shuffle the queue";
    }

}
