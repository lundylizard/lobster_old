package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.QueueUtils;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class QueueCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        var commandHelper = new CommandHelper(event);

        if (!commandHelper.isSelfInVoiceChannel()) {
            event.reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            event.reply(Reply.QUEUE_EMPTY.getMessage()).setEphemeral(true).queue();
            return;
        }

        var pagination = musicManager.scheduler.getPagination();
        var currentTrack = musicManager.audioPlayer.getPlayingTrack();
        var embedBuilder = QueueUtils.generateEmbedFromCurrentPage(pagination, currentTrack);
        event.replyEmbeds(embedBuilder.build()).addActionRow(QueueUtils.generateButtons(pagination)).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("queue", "Display upcoming songs");
    }
}
