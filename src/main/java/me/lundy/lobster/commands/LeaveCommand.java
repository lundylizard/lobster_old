package me.lundy.lobster.commands;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;

@CommandInfo(name = "leave", description = "Make lobster leave your voice channel")
public class LeaveCommand extends Command {

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

        if (!musicManager.scheduler.queue.isEmpty()) {
            musicManager.scheduler.queue.clear();
        }

        musicManager.audioPlayer.destroy();

        context.getGuild().getAudioManager().closeAudioConnection();
        context.getEvent().reply("Left the voice channel").queue();
    }
}
