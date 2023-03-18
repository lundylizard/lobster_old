package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;

public class QueueCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.scheduler.queue.isEmpty()) {
            event.reply(":warning: The queue is currently empty.").setEphemeral(true).queue();
            return;
        }

        AudioTrack[][] trackGroups = BotUtils.splitTracksIntoGroups(musicManager.scheduler.queue);
        int pagesTotal = trackGroups.length;
        double trackCount = 15;
        int queueSize = musicManager.scheduler.queue.size();
        boolean nextPage = queueSize > trackCount;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        embedBuilder.setTitle("Current Queue");

        if (nextPage) {
            embedBuilder.setFooter("Page 1 of " + pagesTotal + " (" + queueSize + " songs total)");
        }

        StringBuilder descriptionBuilder = new StringBuilder();
        int trackIndex = 0;

        for (AudioTrack track : trackGroups[0]) {
            trackIndex++;
            String shortenedTitle = BotUtils.endStringWithEllipsis(track.getInfo().title, 22);
            String shortenedArtist = track.getInfo().author;
            String out = String.format("`#%02d` [%s](%s) | %s `\uD83D\uDD52%s`\n", trackIndex, shortenedTitle, track.getInfo().uri, shortenedArtist, BotUtils.formatTime(track.getDuration()));
            descriptionBuilder.append(out);
        }

        embedBuilder.setDescription(descriptionBuilder.toString());
        createReply(event.replyEmbeds(embedBuilder.build()), nextPage).queue();

    }

    @Override
    public String name() {
        return "queue";
    }

    @Override
    public String description() {
        return "Display the upcoming songs";
    }

    private ReplyCallbackAction createReply(ReplyCallbackAction reply, boolean buttons) {

        if (buttons) {
            reply.addActionRow(
                    Button.secondary("button:queue:previous:0", Emoji.fromUnicode("◀️")).asDisabled(),
                    Button.secondary("button:queue:next:1", Emoji.fromUnicode("▶️"))
            );
        }

        return reply;

    }

}
