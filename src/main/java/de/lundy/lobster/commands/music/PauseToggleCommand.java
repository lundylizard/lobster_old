package de.lundy.lobster.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PauseToggleCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("pause")) {

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

            if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
                event.reply(":warning: You need to be in the same voice channel as me.").setEphemeral(true).queue();
                return;
            }

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            AudioPlayer audioPlayer = musicManager.audioPlayer;

            if (audioPlayer.getPlayingTrack() == null) {
                event.reply(":warning: There is currently no track playing.").setEphemeral(true).queue();
                return;
            }

            boolean paused = audioPlayer.isPaused();
            audioPlayer.setPaused(!paused);

            event.reply(String.format("%s the current song.", paused ? ":pause_button: Paused " : ":arrow_forward: Resumed")).queue();

        }
    }
}

