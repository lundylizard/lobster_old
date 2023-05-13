package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.command.IgnoreChecks;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
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

@CommandInfo(name = "play", description = "Add a song to the song queue")
public class PlayCommand extends Command implements CommandOptions {

    @Override
    @IgnoreChecks
    public void onCommand(SlashCommandInteractionEvent event) {

        event.deferReply().queue();
        OptionMapping searchOption = event.getOption("search");
        OptionMapping topOption = event.getOption("top");

        Guild guild = event.getGuild();
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();
        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply(":warning: You are not in a voice channel.").setEphemeral(true).queue();
            return;
        }

        String searchTerm = searchOption.getAsString();
        boolean isSearchTermValidUrl = isValidUrl(searchTerm);
        String searchQuery = (isSearchTermValidUrl ? searchTerm : "ytsearch:" + searchTerm);

        if (!selfVoiceState.inAudioChannel()) {

            AudioManager audioManager = event.getGuild().getAudioManager();
            AudioChannel audioChannel = member.getVoiceState().getChannel();

            try {
                audioManager.openAudioConnection(audioChannel);
            } catch (InsufficientPermissionException e) {
                event.reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
                return;
            }

        }

        PlayerManager.getInstance().loadAndPlay(event.getHook(), searchQuery, topOption != null && topOption.getAsBoolean());

    }

    private boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public List<OptionData> options() {
        OptionData optionDataSearch = new OptionData(OptionType.STRING, "search", "Search term or url of the song", true);
        OptionData optionDataTop = new OptionData(OptionType.BOOLEAN, "top", "Add this song to the top of the queue");
        return List.of(optionDataSearch, optionDataTop);
    }

}