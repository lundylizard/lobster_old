package me.lundy.lobster.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.IgnoreChecks;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "np", description = "See what song is playing right now")
public class NpCommand extends Command {

    @Override
    @IgnoreChecks
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();

        if (track == null) {
            event.reply(":warning: There is currently no track playing.").setEphemeral(true).queue();
            return;
        }

        AudioTrackInfo trackInfo = track.getInfo();
        String trackUrl = trackInfo.uri;
        String position = BotUtils.getTrackPosition(track);
        String repeatingString = musicManager.scheduler.isRepeating() ? " :repeat:" : "";
        event.reply(String.format("%s | `%s`%s", trackUrl, position, repeatingString)).queue();

    }

}
