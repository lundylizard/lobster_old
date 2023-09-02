package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PauseCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(Reply.EXECUTOR_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        if (!context.selfInVoice()) {
            context.getEvent().reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        if (!context.inSameVoice()) {
            context.getEvent().reply(Reply.NOT_IN_SAME_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        boolean paused = musicManager.audioPlayer.isPaused();
        musicManager.audioPlayer.setPaused(!paused);
        String pauseState = musicManager.audioPlayer.isPaused() ? "Paused" : "Unpaused";
        context.getEvent().replyFormat(Reply.PAUSED.getMessage(), pauseState).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("pause", "Pause / Unpause the music player");
    }
}
