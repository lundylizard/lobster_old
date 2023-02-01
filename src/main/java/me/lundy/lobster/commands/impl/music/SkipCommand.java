package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SkipCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        String oldTrack = musicManager.audioPlayer.getPlayingTrack().getInfo().title;
        musicManager.scheduler.nextTrack();
        String newTrack = "";
        if (musicManager.audioPlayer.getPlayingTrack() != null) {
            newTrack = musicManager.audioPlayer.getPlayingTrack().getInfo().title;
        }
        event.reply(String.format("Skipping `%s`...%s", oldTrack,
                newTrack.equals("") ? "" : "\n:musical_note: Now Playing: `" + newTrack + "`")).queue();
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


