package me.lundy.lobster.listeners;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Pagination;
import me.lundy.lobster.utils.QueueUtils;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class QueueButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        var guild = event.getGuild();
        if (guild == null) return;

        String buttonId = event.getComponentId();

        if (buttonId.startsWith("button:queue")) {

            var musicManager = PlayerManager.getInstance().getMusicManager(guild);
            var pagination = getAudioTrackPagination(musicManager, buttonId);

            if (musicManager.scheduler.queue.isEmpty()) {
                event.reply(Reply.QUEUE_EMPTY.getMessage()).setEphemeral(true).queue();
                return;
            }

            var currentTrack = musicManager.audioPlayer.getPlayingTrack();
            var embed = QueueUtils.generateEmbedFromCurrentPage(pagination, currentTrack);
            event.editMessageEmbeds(embed.build()).setActionRow(QueueUtils.generateButtons(pagination)).queue();
        }
    }

    @NotNull
    private Pagination<AudioTrack> getAudioTrackPagination(GuildMusicManager musicManager, String buttonId) {
        var pagination = musicManager.scheduler.getPagination();
        switch (buttonId) {
            case "button:queue:first" -> pagination.firstPage();
            case "button:queue:last" -> pagination.lastPage();
            case "button:queue:previous" -> pagination.previousPage();
            case "button:queue:next" -> pagination.nextPage();
        }
        return pagination;
    }

}
