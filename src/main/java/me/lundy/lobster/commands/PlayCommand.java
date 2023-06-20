package me.lundy.lobster.commands;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
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
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(":warning: You are not in a voice channel").setEphemeral(true).queue();
            return;
        }

        OptionMapping topOption = context.getEvent().getOption("top");
        String searchTerm = context.getEvent().getOption("search").getAsString();
        boolean isSearchTermValidUrl = isValidUrl(searchTerm);
        String searchQuery = (isSearchTermValidUrl ? searchTerm : "ytsearch:" + searchTerm);

        if (!context.selfInVoice()) {
            AudioManager audioManager = context.getGuild().getAudioManager();
            AudioChannel audioChannel = context.getExecutorVoiceState().getChannel();

            try {
                audioManager.openAudioConnection(audioChannel);
            } catch (InsufficientPermissionException e) {
                context.getEvent().reply(":warning: I do not have enough permissions to join that channel").queue();
                return;
            }
        }

        PlayerManager.getInstance().loadAndPlay(context, searchQuery, topOption != null && topOption.getAsBoolean());
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