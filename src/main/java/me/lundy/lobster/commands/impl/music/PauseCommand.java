package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PauseCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        boolean paused = musicManager.audioPlayer.isPaused();
        musicManager.audioPlayer.setPaused(!paused);
        event.reply(String.format("%s current song.", musicManager.audioPlayer.isPaused() ? "Paused" : "Unpaused")).queue();

    }

    @Override
    public String name() {
        return "pause";
    }

    @Override
    public String description() {
        return "Pause / Unpause the current song.";
    }
}
