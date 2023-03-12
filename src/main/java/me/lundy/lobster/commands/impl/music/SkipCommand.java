package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SkipCommand extends BotCommand {

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
        }

        event.reply(message).queue();

    }

    @Override
    public String name() {
        return "skip";
    }

    @Override
    public String description() {
        return "Skip the current song";
    }

}


