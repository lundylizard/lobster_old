package me.lundy.lobster.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import me.lundy.lobster.utils.QueueUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "queue", description = "Display a list of upcoming songs")
public class QueueCommand extends Command {

    @Override
    @RunCheck(check = CommandCheck.IN_SAME_VOICE)
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            event.reply(":warning: The queue is currently empty.").setEphemeral(true).queue();
            return;
        }

        AudioTrack[][] trackGroups = QueueUtils.splitTracksIntoGroups(musicManager.scheduler.queue);
        int pagesTotal = trackGroups.length;
        int queueSize = musicManager.scheduler.queue.size();
        long totalLength = musicManager.scheduler.queue.stream().mapToLong(AudioTrack::getDuration).sum();
        AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        embedBuilder.setTitle("Current Queue");
        embedBuilder.setFooter(QueueUtils.generateFooter(0, pagesTotal, queueSize, BotUtils.convertMillisecondsToHoursMinutes(totalLength)));
        if (currentTrack != null) embedBuilder.appendDescription(QueueUtils.generateCurrentTrack(currentTrack));

        int trackIndex = 0;
        for (AudioTrack track : trackGroups[0]) {
            trackIndex++;
            embedBuilder.appendDescription(QueueUtils.generateQueueTrack(trackIndex, track));
            embedBuilder.appendDescription("\n");
        }

        event.replyEmbeds(embedBuilder.build()).addActionRow(QueueUtils.generateButtons(pagesTotal, 0)).queue();
    }
}
