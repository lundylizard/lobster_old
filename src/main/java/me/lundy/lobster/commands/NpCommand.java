package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class NpCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.selfInVoice()) {
            context.getEvent().reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();

        if (track == null) {
            context.getEvent().reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
            return;
        }

        AudioTrackInfo trackInfo = track.getInfo();
        String trackUrl = trackInfo.uri;
        String position = StringUtils.getTrackPosition(track);
        String repeatingString = musicManager.scheduler.isRepeating() ? " :repeat:" : "";
        String pauseString = musicManager.audioPlayer.isPaused() ? " :pause_button:" : "";
        context.getEvent().reply(String.format("%s | `%s`%s%s", trackUrl, position, repeatingString, pauseString)).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("np", "Find out what's playing right now");
    }
}
