package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.commands.IgnoreChecks;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URL;
import java.util.List;

@IgnoreChecks
public class PlayCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        event.deferReply().queue();
        OptionMapping searchOption = event.getOption("search");
        OptionMapping topOption = event.getOption("top");
        Member member = event.getMember();

        if (!member.getVoiceState().inAudioChannel()) {
            event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
            return;
        }

        String search = (!isUrl(searchOption.getAsString()) ? "ytsearch:" : "") + searchOption.getAsString();

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

        PlayerManager.getInstance().loadAndPlay(event.getHook(), search, topOption != null && topOption.getAsBoolean());
    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public String name() {
        return "play";
    }

    @Override
    public String description() {
        return "Add a song to the song queue.";
    }

    @Override
    public List<OptionData> options() {
        OptionData optionDataSearch = new OptionData(OptionType.STRING, "search", "Search term or url of the song", true);
        OptionData optionDataTop = new OptionData(OptionType.BOOLEAN, "top", "Add this song to the top of the queue");
        return List.of(optionDataSearch, optionDataTop);
    }

}