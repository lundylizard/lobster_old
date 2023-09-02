package me.lundy.lobster.listeners.buttons;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.QueueUtils;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QueueButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (event.getGuild() == null) return;

        String buttonId = event.getComponentId();

        if (buttonId.startsWith("button:queue")) {

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            int queueSize = musicManager.scheduler.queue.size();
            String currentPageString = buttonId.substring(buttonId.lastIndexOf(":") + 1);
            int pagesTotal = QueueUtils.splitTracksIntoGroups(musicManager.scheduler.queue).length;
            int currentPage;

            if (currentPageString.equalsIgnoreCase("first")) {
                currentPage = 0;
            } else if (currentPageString.equalsIgnoreCase("last")) {
                currentPage = pagesTotal - 1;
            } else {
                currentPage = Integer.parseInt(buttonId.substring(buttonId.lastIndexOf(":") + 1));
            }

            if (musicManager.scheduler.queue.isEmpty()) {
                event.reply(Reply.QUEUE_EMPTY.getMessage()).setEphemeral(true).queue();
                return;
            }

            if (queueSize <= QueueUtils.TRACKS_PER_PAGE) {
                event.reply(Reply.QUEUE_OUTDATED.getMessage()).setEphemeral(true).queue();
                return;
            }

            AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
            embedBuilder.setTitle("Current Queue");
            long totalLength = musicManager.scheduler.queue.stream().mapToLong(AudioTrack::getDuration).sum();
            embedBuilder.setFooter(QueueUtils.generateFooter(currentPage, pagesTotal, queueSize, StringUtils.convertMsToHoursAndMinutes(totalLength)));
            if (currentTrack != null) embedBuilder.appendDescription(QueueUtils.generateCurrentTrack(currentTrack));

            int trackIndex = QueueUtils.TRACKS_PER_PAGE * currentPage;
            int tracksOnLastPage = queueSize % QueueUtils.TRACKS_PER_PAGE;
            int trackCountToDisplay = currentPage == pagesTotal ? tracksOnLastPage : QueueUtils.TRACKS_PER_PAGE;

            AudioTrack[] trackGroup = QueueUtils.splitTracksIntoGroups(musicManager.scheduler.queue)[currentPage];
            for (int i = 0; i < trackCountToDisplay; i++) {
                AudioTrack track = trackGroup[i];
                trackIndex++;
                String queueTrackString = QueueUtils.generateQueueTrack(trackIndex, track);
                embedBuilder.appendDescription(queueTrackString).appendDescription("\n");
            }

            event.editMessageEmbeds(embedBuilder.build()).setActionRow(QueueUtils.generateButtons(pagesTotal, currentPage)).queue();
        }
    }

}
