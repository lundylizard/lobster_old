package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SkipCommand extends BotCommand {

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
        AudioTrack oldTrack = musicManager.audioPlayer.getPlayingTrack();

        if (oldTrack == null) {
            context.getEvent().reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        AudioTrack newTrack = musicManager.audioPlayer.getPlayingTrack();
        String message = String.format(Reply.TRACK_SKIPPED.getMessage(), oldTrack.getInfo().title, oldTrack.getInfo().author);

        if (newTrack != null) {
            message += String.format(Reply.SKIP_NEXT_SONG.getMessage(), newTrack.getInfo().title, newTrack.getInfo().author);
        }

        context.getEvent().reply(message).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("skip", "Skip the current song");
    }
}
