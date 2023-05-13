package me.lundy.lobster.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "skip", description = "Skip the current song")
public class SkipCommand extends Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrack oldTrack = musicManager.audioPlayer.getPlayingTrack();

        if (oldTrack == null) {
            event.reply(":warning: There is no track currently playing.").setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        AudioTrack newTrack = musicManager.audioPlayer.getPlayingTrack();

        String message = String.format("Skipping `%s`...", oldTrack.getInfo().title);

        if (newTrack != null) {
            message += String.format("\n:musical_note: Now Playing: `%s`", newTrack.getInfo().title);
            message = "> " + message;
        }

        event.reply(message).queue();

    }

}


