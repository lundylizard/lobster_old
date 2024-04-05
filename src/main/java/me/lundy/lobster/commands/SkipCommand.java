package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SkipCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        var commandHelper = new CommandHelper(event);

        if (!commandHelper.isExecutorInVoiceChannel()) {
            event.reply(Reply.EXECUTOR_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        if (!commandHelper.isSelfInVoiceChannel()) {
            event.reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        if (!commandHelper.inSameVoice()) {
            event.reply(Reply.NOT_IN_SAME_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var oldTrack = musicManager.audioPlayer.getPlayingTrack();

        if (oldTrack == null) {
            event.reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        var newTrack = musicManager.audioPlayer.getPlayingTrack();
        String message = String.format(Reply.TRACK_SKIPPED.getMessage(), oldTrack.getInfo().title, oldTrack.getInfo().author);

        if (newTrack != null) {
            message += String.format(Reply.SKIP_NEXT_SONG.getMessage(), newTrack.getInfo().title, newTrack.getInfo().author);
        }

        event.reply(message).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("skip", "Skip the current song");
    }
}
