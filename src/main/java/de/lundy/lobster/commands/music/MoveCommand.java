package de.lundy.lobster.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoveCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("move")) {

            Member self = event.getGuild().getSelfMember();
            GuildVoiceState selfVoiceState = self.getVoiceState();

            if (!selfVoiceState.inAudioChannel()) {
                event.reply(":warning: I am not playing anything.").setEphemeral(true).queue();
                return;
            }

            Member member = event.getMember();
            GuildVoiceState memberVoiceState = member.getVoiceState();

            if (!memberVoiceState.inAudioChannel()) {
                event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
                return;
            }

            if (!Objects.equals(selfVoiceState.getChannel(), memberVoiceState.getChannel())) {
                event.reply(":warning: We are not in the same voice channel.").setEphemeral(true).queue();
                return;
            }

            OptionMapping optionFrom = event.getOption("from");
            OptionMapping optionTo = event.getOption("to");

            if (optionFrom == null || optionTo == null) return;

            int from = optionFrom.getAsInt();
            int to = optionTo.getAsInt();

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

            if (from > musicManager.scheduler.queue.size()) {
                event.reply(String.format(":warning: Track `#%d` is not in the queue.", from)).setEphemeral(true).queue();
                return;
            }

            if (to > musicManager.scheduler.queue.size()) {
                to = musicManager.scheduler.queue.size();
            }

            List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
            trackList.add(to - 1, trackList.get(from - 1));
            trackList.remove(from);
            musicManager.scheduler.queue.clear();
            musicManager.scheduler.queue.addAll(trackList);
            event.reply(String.format("Successfully moved track `#%d` to `#%d`", from, to)).queue();

        }

    }
}
