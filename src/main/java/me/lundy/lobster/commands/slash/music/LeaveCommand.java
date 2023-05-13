package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "leave", description = "Make lobster leave your voice channel")
public class LeaveCommand extends Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (!musicManager.scheduler.queue.isEmpty()) {
            musicManager.scheduler.queue.clear();
        }

        musicManager.audioPlayer.destroy();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply("Left the voice channel.").queue();
    }

}
