package me.lundy.lobster.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "shuffle", description = "Shuffle the queue")
public class ShuffleCommand extends Command {

    @Override
    @RunCheck(check = CommandCheck.IN_SAME_VOICE)
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        Collections.shuffle(trackList);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);
        event.reply("Shuffled the queue.").queue();
    }

}
