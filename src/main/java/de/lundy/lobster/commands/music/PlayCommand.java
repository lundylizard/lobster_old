package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class PlayCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) return;

        if (event.getName().equalsIgnoreCase("play")) {

            OptionMapping searchOption = event.getOption("search");
            OptionMapping topOption = event.getOption("top");
            OptionMapping attachmentOption = event.getOption("attachment");
            Member member = event.getMember();

            if (!member.getVoiceState().inAudioChannel()) {
                event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
                return;
            }

            if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {

                AudioManager audioManager = event.getGuild().getAudioManager();
                AudioChannel audioChannel = member.getVoiceState().getChannel();

                try {
                    audioManager.openAudioConnection(audioChannel);
                } catch (InsufficientPermissionException e) {
                    event.reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
                    return;
                }

            }

            if (searchOption == null && attachmentOption == null) {
                event.reply(":warning: Please enter a `search` or provide an `attachment`.").setEphemeral(true).queue();
                return;
            }

            String search = "";

            if (attachmentOption != null) {
                search = attachmentOption.getAsAttachment().getUrl();
            }

            if (searchOption != null)
                search = (!isUrl(searchOption.getAsString()) ? "ytsearch:" : "") + searchOption.getAsString();

            event.deferReply().queue();
            PlayerManager.getInstance().loadAndPlay(event, search, topOption != null && topOption.getAsBoolean());

        }

    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }
}