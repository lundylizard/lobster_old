package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;

public class PlayCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        var commandHelper = new CommandHelper(event);

        if (!commandHelper.isExecutorInVoiceChannel()) {
            event.reply(Reply.EXECUTOR_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();
        OptionMapping topOption = event.getOption("top");
        String searchTerm = event.getOption("search").getAsString();
        boolean isSearchTermValidUrl = isValidUrl(searchTerm);
        String searchQuery = (isSearchTermValidUrl ? searchTerm : "ytsearch:" + searchTerm);

        if (!commandHelper.isSelfInVoiceChannel()) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            AudioChannel audioChannel = commandHelper.getExecutorVoiceChannel();

            try {
                audioManager.openAudioConnection(audioChannel);
            } catch (InsufficientPermissionException e) {
                event.reply(Reply.ERROR_NO_PERMISSIONS_VOICE.getMessage()).queue();
                return;
            }
        }

        PlayerManager.getInstance().loadAndPlay(event, searchQuery, topOption != null && topOption.getAsBoolean());
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSearch = new OptionData(OptionType.STRING, "search", "Search term or url of the song", true);
        OptionData optionTop = new OptionData(OptionType.BOOLEAN, "top", "Add this song to the top of the queue");
        return Commands.slash("play", "Add a song to the queue").addOptions(optionSearch, optionTop);
    }

    private boolean isValidUrl(String urlString) {
        try {
            var ignored = URI.create(urlString);
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

}