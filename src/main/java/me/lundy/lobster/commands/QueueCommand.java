package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.QueueUtils;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class QueueCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.selfInVoice()) {
            context.getEvent().reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            context.getEvent().reply(Reply.QUEUE_EMPTY.getMessage()).setEphemeral(true).queue();
            return;
        }

        AudioTrack[][] trackGroups = QueueUtils.splitTracksIntoGroups(musicManager.scheduler.queue);
        int pagesTotal = trackGroups.length;
        int queueSize = musicManager.scheduler.queue.size();
        long totalLength = musicManager.scheduler.queue.stream().mapToLong(AudioTrack::getDuration).sum();
        AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(context.getGuild().getSelfMember().getColor());
        embedBuilder.setTitle("Current Queue");
        embedBuilder.setFooter(QueueUtils.generateFooter(0, pagesTotal, queueSize, StringUtils.convertMsToHoursAndMinutes(totalLength)));
        if (currentTrack != null) embedBuilder.appendDescription(QueueUtils.generateCurrentTrack(currentTrack));

        int trackIndex = 0;
        for (AudioTrack track : trackGroups[0]) {
            trackIndex++;
            embedBuilder.appendDescription(QueueUtils.generateQueueTrack(trackIndex, track));
            embedBuilder.appendDescription("\n");
        }

        context.getEvent().replyEmbeds(embedBuilder.build()).addActionRow(QueueUtils.generateButtons(pagesTotal, 0)).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("queue", "Display upcoming songs");
    }
}
