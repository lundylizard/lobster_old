package me.lundy.lobster.listeners;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class QueueButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (event.getGuild() == null) return;

        String buttonId = event.getComponentId();

        if (buttonId.startsWith("button:queue")) {

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            int queueSize = musicManager.scheduler.queue.size();

            if (queueSize < 16) {
                long queueCommandId = Lobster.getInstance().getCommandManager().getCommands().get("queue").getId();
                event.reply(":warning: The queue list is outdated by now. Please run </queue:" + queueCommandId + ">again.").setEphemeral(true).queue();
                return;
            }

            int currentPage = Integer.parseInt(buttonId.substring(buttonId.lastIndexOf(":") + 1));
            AudioTrack[] trackGroup = BotUtils.splitTracksIntoGroups(musicManager.scheduler.queue)[currentPage];
            int trackCount = 15;
            int pagesTotal = BotUtils.splitTracksIntoGroups(musicManager.scheduler.queue).length;

            if (musicManager.scheduler.queue.isEmpty()) {
                event.reply(":warning: The queue is currently empty.").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
            embedBuilder.setTitle("Current Queue");

            boolean nextPageAvailable = currentPage < pagesTotal - 1; // check if current page is not the last page
            if (nextPageAvailable) {
                embedBuilder.setFooter(String.format("Page %d of %d (%d songs total)", (currentPage + 1), pagesTotal, queueSize));
            } else {
                embedBuilder.setFooter(String.format("Page %d of %d (%d songs total)", pagesTotal, pagesTotal, queueSize)); // set footer to last page
            }

            int trackIndex = 15 * currentPage;
            int tracksOnLastPage = queueSize % trackCount;
            int trackCountToDisplay = currentPage == pagesTotal - 1 ? tracksOnLastPage : trackCount;

            for (int i = 0; i < trackCountToDisplay; i++) {
                AudioTrack track = trackGroup[i];
                trackIndex++;
                String shortenedTitle = BotUtils.endStringWithEllipsis(track.getInfo().title, 22);
                String shortenedArtist = track.getInfo().author;
                String out = String.format("`#%02d` [%s](%s) | %s `\uD83D\uDD52%s`", trackIndex, shortenedTitle, track.getInfo().uri, shortenedArtist, BotUtils.formatTime(track.getDuration()));
                embedBuilder.appendDescription(out).appendDescription("\n");
            }

            event.editMessageEmbeds(embedBuilder.build()).setActionRow(generateButtons(currentPage, pagesTotal)).queue();
        }
    }


    private List<Button> generateButtons(int currentPage, int pagesTotal) {
        List<Button> buttons = new ArrayList<>();
        Button previousPageButton = Button.secondary("button:queue:previous:" + (currentPage - 1), Emoji.fromUnicode("◀️"));
        Button nextPageButton = Button.secondary("button:queue:next:" + (currentPage + 1), Emoji.fromUnicode("▶️"));
        if (currentPage == 0) {
            buttons.add(previousPageButton.asDisabled()); // disable the previous page button on the first page
        } else {
            buttons.add(previousPageButton);
        }
        if (currentPage == pagesTotal - 1) {
            buttons.add(nextPageButton.asDisabled()); // disable the next page button on the last page
        } else {
            buttons.add(nextPageButton);
        }
        return buttons;
    }

}
