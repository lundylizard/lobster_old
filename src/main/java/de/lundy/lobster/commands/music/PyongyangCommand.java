package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

public class PyongyangCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("pyongyang")) {

            GuildVoiceState selfVoiceState = event.getGuild().getSelfMember().getVoiceState();
            GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

            if (!memberVoiceState.inAudioChannel()) {
                event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
                return;
            }

            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                AudioManager audioManager = event.getGuild().getAudioManager();
                AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();

                try {
                    audioManager.openAudioConnection(audioChannel);
                } catch (InsufficientPermissionException e) {
                    event.reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
                    return;
                }

            }

            String radioUrl = "https://radio.garden/api/ara/content/listen/NPzavm5p/channel.mp3";
            event.deferReply().queue();
            PlayerManager.getInstance().loadAndPlay(event, radioUrl, true);

        }

    }
}
