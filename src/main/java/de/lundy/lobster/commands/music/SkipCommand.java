package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.GuildMusicManager;
import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SkipCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("skip")) {

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
            event.reply(String.format("Skipping %s...", musicManager.audioPlayer.getPlayingTrack().getInfo().title)).queue();
            musicManager.scheduler.nextTrack();

        }

    }
}


