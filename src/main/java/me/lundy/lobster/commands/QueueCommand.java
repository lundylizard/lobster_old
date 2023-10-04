package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Pagination;
import me.lundy.lobster.utils.QueueUtils;
import me.lundy.lobster.utils.Reply;
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

        Pagination<AudioTrack> pagination = musicManager.scheduler.getPagination();
        AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
        EmbedBuilder embedBuilder = QueueUtils.generateEmbedFromCurrentPage(pagination, currentTrack);
        context.getEvent().replyEmbeds(embedBuilder.build()).addActionRow(QueueUtils.generateButtons(pagination)).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("queue", "Display upcoming songs");
    }
}
