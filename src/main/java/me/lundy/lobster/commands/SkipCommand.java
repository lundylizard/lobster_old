package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;

@CommandInfo(name = "skip", description = "Skip the current song")
public class SkipCommand extends Command {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(":warning: You are not in a voice channel").setEphemeral(true).queue();
            return;
        }

        if (!context.selfInVoice()) {
            context.getEvent().reply(":warning: I am not in a voice channel").setEphemeral(true).queue();
            return;
        }

        if (!context.inSameVoice()) {
            context.getEvent().reply(":warning: We are not in the same voice channel").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack oldTrack = musicManager.audioPlayer.getPlayingTrack();

        if (oldTrack == null) {
            context.getEvent().reply(":warning: There is currently no track playing").setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        AudioTrack newTrack = musicManager.audioPlayer.getPlayingTrack();
        String message = String.format("Skipping `%s`...", oldTrack.getInfo().title);

        if (newTrack != null) {
            message += String.format("\n:musical_note: Now Playing: `%s`", newTrack.getInfo().title);
            message = "> " + message;
        }

        context.getEvent().reply(message).queue();
    }
}
