package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.StringUtils;

@CommandInfo(name = "np", description = "See what song is playing right now")
public class NpCommand extends Command {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.selfInVoice()) {
            context.getEvent().reply(":warning: I am not in a voice channel").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();

        if (track == null) {
            context.getEvent().reply(":warning: There is currently no track playing").setEphemeral(true).queue();
            return;
        }

        AudioTrackInfo trackInfo = track.getInfo();
        String trackUrl = trackInfo.uri;
        String position = StringUtils.getTrackPosition(track);
        String repeatingString = musicManager.scheduler.isRepeating() ? " :repeat:" : "";
        context.getEvent().reply(String.format("%s | `%s`%s", trackUrl, position, repeatingString)).queue();
    }
}
