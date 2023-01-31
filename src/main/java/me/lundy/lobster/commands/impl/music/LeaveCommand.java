package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LeaveCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (!musicManager.scheduler.queue.isEmpty()) {
            musicManager.scheduler.queue.clear();
        }

        if (musicManager.audioPlayer.getPlayingTrack() != null) {
            musicManager.audioPlayer.getPlayingTrack().stop();
        }

        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply("Left the voice channel.").queue();
    }

    @Override
    public String name() {
        return "leave";
    }

    @Override
    public String description() {
        return "Let lobster leave the voice channel";
    }

}
