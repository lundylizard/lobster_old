package me.lundy.lobster.listeners;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Pagination;
import me.lundy.lobster.utils.QueueUtils;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class QueueButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        Guild guild = event.getGuild();
        if (guild == null) return;

        String buttonId = event.getComponentId();

        if (buttonId.startsWith("button:queue")) {

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
            Pagination<AudioTrack> pagination = getAudioTrackPagination(musicManager, buttonId);

            if (musicManager.scheduler.queue.isEmpty()) {
                event.reply(Reply.QUEUE_EMPTY.getMessage()).setEphemeral(true).queue();
                return;
            }

            AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
            EmbedBuilder embed = QueueUtils.generateEmbedFromCurrentPage(pagination, currentTrack);
            event.editMessageEmbeds(embed.build()).setActionRow(QueueUtils.generateButtons(pagination)).queue();
        }
    }

    @NotNull
    private Pagination<AudioTrack> getAudioTrackPagination(GuildMusicManager musicManager, String buttonId) {
        Pagination<AudioTrack> pagination = musicManager.scheduler.getPagination();
        switch (buttonId) {
            case "button:queue:first" -> pagination.firstPage();
            case "button:queue:last" -> pagination.lastPage();
            case "button:queue:previous" -> pagination.previousPage();
            case "button:queue:next" -> pagination.nextPage();
        }
        return pagination;
    }

}
