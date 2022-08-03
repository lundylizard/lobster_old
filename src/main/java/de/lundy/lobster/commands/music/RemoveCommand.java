package de.lundy.lobster.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RemoveCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("remove")) {

            OptionMapping indexOption = event.getOption("index");
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

            int index = indexOption.getAsInt();

            if (index > musicManager.scheduler.queue.size() || index < musicManager.scheduler.queue.size()) {
                event.reply(":warning: This song is not in the queue.").setEphemeral(true).queue();
                return;
            }

            List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
            musicManager.scheduler.queue.removeFirstOccurrence(trackList.get(index - 1));
            event.reply(String.format("Removed song #%d from the queue.", index)).queue();

        }
    }
}
