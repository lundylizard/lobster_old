package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LeaveCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("leave")) {

            Member self = event.getGuild().getSelfMember();
            GuildVoiceState selfVoiceState = self.getVoiceState();
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
                event.reply(":warning: You have to be in the same voice channel as me.").setEphemeral(true).queue();
                return;
            }

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            if (musicManager.scheduler.queue.size() > 0) musicManager.scheduler.queue.clear();
            if (musicManager.audioPlayer.getPlayingTrack() != null) musicManager.audioPlayer.getPlayingTrack().stop();
            event.getGuild().getAudioManager().closeAudioConnection();
            event.reply("Left the voice channel.").queue();

        }

    }
}
