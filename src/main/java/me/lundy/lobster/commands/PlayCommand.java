package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URL;

public class PlayCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(Reply.EXECUTOR_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
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
                context.getEvent().reply(Reply.ERROR_NO_PERMISSIONS_VOICE.getMessage()).queue();
                return;
            }
        }

        PlayerManager.getInstance().loadAndPlay(context, searchQuery, topOption != null && topOption.getAsBoolean());
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSearch = new OptionData(OptionType.STRING, "search", "Search term or url of the song", true);
        OptionData optionTop = new OptionData(OptionType.BOOLEAN, "top", "Add this song to the top of the queue");
        // SubcommandData attachmentSubcommand = new SubcommandData("attachment", "Play a file")
        //         .addOptions(new OptionData(OptionType.ATTACHMENT, "file", "File to play", true))
        //         .addOptions(new OptionData(OptionType.BOOLEAN, "top", "Add this song to the top of the queue"));
        return Commands.slash("play", "Add a song to the queue").addOptions(optionSearch, optionTop);
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

}