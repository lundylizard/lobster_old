package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NowPlayingCommand extends BotCommand {

    @Override
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

    @Override
    public String name() {
        return "np";
    }

    @Override
    public String description() {
        return "See what song is playing right now";
    }

}
