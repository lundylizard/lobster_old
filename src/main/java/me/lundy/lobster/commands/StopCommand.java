package me.lundy.lobster.commands;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;

@CommandInfo(name = "stop", description = "Stop the music and clear the queue")
public class StopCommand extends Command {

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
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.stopTrack();
        context.getEvent().reply("Stopped the playback.").queue();
    }

}

