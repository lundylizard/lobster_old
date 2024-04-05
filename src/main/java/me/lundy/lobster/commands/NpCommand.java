package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class NpCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        var commandHelper = new CommandHelper(event);

        if (!commandHelper.isSelfInVoiceChannel()) {
            event.reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var track = musicManager.audioPlayer.getPlayingTrack();

        if (track == null) {
            event.reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
            return;
        }

        var trackInfo = track.getInfo();
        String position = StringUtils.getTrackPosition(track);
        String repeatingString = musicManager.scheduler.isRepeating() ? " :repeat:" : "";
        String pauseString = musicManager.audioPlayer.isPaused() ? " :pause_button:" : "";
        event.reply(trackInfo.uri + " | `" + position + "`" +  repeatingString + " " + pauseString).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("np", "Find out what's playing right now");
    }
}
