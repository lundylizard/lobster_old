package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VolumeCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("volume")) {

            OptionMapping volumeOption = event.getOption("amount");
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

            if (volumeOption == null) {
                event.reply(String.format("Current volume is %d", musicManager.audioPlayer.getVolume())).setEphemeral(true).queue();
                return;
            }

            int newVolume = volumeOption.getAsInt();

            if (newVolume <= 0) {
                event.reply("Volume cannot be 0 or lower.").setEphemeral(true).queue();
                return;
            }

            boolean probablyLoud = newVolume >= 250;
            musicManager.audioPlayer.setVolume(newVolume);
            event.reply(String.format("Changed the volume to %d%s", newVolume, probablyLoud ? "% - This might be loud." : "%")).queue();

        }

    }
}
