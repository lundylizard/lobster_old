package de.lundy.lobster.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueueCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("queue")) {

            GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
            GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

            if (!selfVoiceState.inAudioChannel()) {
                event.reply(":warning: I am not in a voice channel.").setEphemeral(true).queue();
                return;
            }

            if (!memberVoiceState.inAudioChannel()) {
                event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
                return;
            }

            if (!Objects.equals(selfVoiceState.getChannel(), memberVoiceState.getChannel())) {
                event.reply(":warning: We are not in the same voice channel.").setEphemeral(true).queue();
                return;
            }

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
                System.out.println(out.length());
            }

            if (trackList.size() > trackCount) {
                String out = String.format("and %d more...", (trackList.size() - trackCount));
                queueOutput.append("\n").append(out);
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription(queueOutput);
            embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
            embedBuilder.setFooter("Page 1 of " + Math.ceil(musicManager.scheduler.queue.size() / 10d));
            event.replyEmbeds(embedBuilder.build()).queue();

        }

    }
}
