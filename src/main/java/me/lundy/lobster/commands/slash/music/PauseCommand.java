package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "pause", description = "Pause / Unpause the current song")
public class PauseCommand extends Command {

    @Override
    @RunCheck(check = CommandCheck.IN_SAME_VOICE)
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        boolean paused = musicManager.audioPlayer.isPaused();
        musicManager.audioPlayer.setPaused(!paused);
        event.reply(String.format("%s current song.", musicManager.audioPlayer.isPaused() ? "Paused" : "Unpaused")).queue();
    }

}
