package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class QueueCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            event.reply(":warning: The queue is currently empty.").setEphemeral(true).queue();
            return;
        }

        int trackCount = Math.min(musicManager.scheduler.queue.size(), 10);
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        StringBuilder queueOutput = new StringBuilder();

        for (int i = 0; i < trackCount; i++) {
            AudioTrack track = trackList.get(i);
            String out = String.format("`#%d` [%s](%s) | %s", (i + 1), track.getInfo().title, track.getInfo().uri, track.getInfo().author);
            queueOutput.append(out).append("\n");
        }

        if (trackList.size() > trackCount) {
            String out = String.format("and %d more...", (trackList.size() - trackCount));
            queueOutput.append("\n").append(out);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(queueOutput);
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String name() {
        return "queue";
    }

    @Override
    public String description() {
        return "Display the upcoming songs";
    }

}
