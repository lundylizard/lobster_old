package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "stop", description = "Stop the music and clear the queue")
public class StopCommand extends Command {

    @Override
    @RunCheck(check = CommandCheck.IN_SAME_VOICE)
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.stopTrack();
        event.reply("Stopped the playback.").queue();
    }

}

