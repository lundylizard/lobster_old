package de.lundy.lobster.commands.music;

import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

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

            String search = (!isUrl(searchOption.getAsString()) ? "ytsearch:" : "") + searchOption.getAsString();

            if (attachmentOption != null) {
                Message.Attachment attachment = attachmentOption.getAsAttachment();
                if (BotUtils.allowedFileExtensions().contains(attachment.getFileExtension())) {
                    String allowedFileExtensions = String.join(", ", BotUtils.allowedFileExtensions());
                    event.reply(":warning: File attached is invalid. Valid file formats are: " + allowedFileExtensions).setEphemeral(true).queue();
                    return;
                }
                search = attachmentOption.getAsAttachment().getUrl();
            }

            PlayerManager.getInstance().loadAndPlay(event, search, topOption != null && topOption.getAsBoolean());

        }

    }

    // TODO replace with pattern and move to botutils class
    private boolean isUrl(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }
}