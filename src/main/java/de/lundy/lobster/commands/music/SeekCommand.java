package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SeekCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("seek")) {

            OptionMapping seekOption = event.getOption("amount");
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

            if (musicManager.audioPlayer.getPlayingTrack() == null) {
                event.reply(":warning: There is currently no song playing.").setEphemeral(true).queue();
                return;
            }

            int seekAmount = seekOption.getAsInt() * 1000;
            long newPos = musicManager.audioPlayer.getPlayingTrack().getPosition() + seekAmount;

            if (newPos < 0) {
                newPos = 0L;
            }

            if (newPos > musicManager.audioPlayer.getPlayingTrack().getDuration()) {
                musicManager.scheduler.nextTrack();
                event.reply(String.format("Skipped `%s`", musicManager.audioPlayer.getPlayingTrack().getInfo().title)).queue();
                return;
            }

            musicManager.audioPlayer.getPlayingTrack().setPosition(newPos);
            String position = BotUtils.formatTime(musicManager.audioPlayer.getPlayingTrack().getPosition());
            event.reply(String.format("Set song position to **%s**", position)).queue();

        }

    }
}