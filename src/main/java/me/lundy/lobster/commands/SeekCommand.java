package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SeekCommand extends BotCommand {

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
        var track = musicManager.audioPlayer.getPlayingTrack();

        if (track == null) {
            event.reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
            return;
        }

        var newTime = event.getOption("position").getAsString();

        if (!StringUtils.isValidTimeFormat(newTime)) {
            event.reply(Reply.INVALID_TIME_FORMAT.getMessage()).queue();
            return;
        }

        int newSeconds = StringUtils.timeToSeconds(newTime);
        long newPos = newSeconds * 1000L;
        newPos = Math.max(0L, newPos);

        if (newPos >= track.getDuration()) {
            musicManager.scheduler.nextTrack();
            event.replyFormat(Reply.TRACK_SKIPPED.getMessage(), track.getInfo().title, track.getInfo().author).queue();
        } else {
            track.setPosition(newPos);
            event.replyFormat(Reply.TRACK_SET_POSITION.getMessage(), StringUtils.formatTime(newPos)).queue();
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        var optionAmount = new OptionData(OptionType.STRING, "position", "New time of song", true, true);
        return Commands.slash("seek", "Change the time of the current song").addOptions(optionAmount);
    }

    @Override
    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        var guild = event.getGuild();
        if (guild == null) return null;
        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var track = musicManager.audioPlayer.getPlayingTrack();
        if (track == null) return Collections.emptyList();
        long max = track.getDuration();
        return Stream.of(StringUtils.getAvailableTimes(max))
                .limit(25)
                .map(time -> new Command.Choice(time, time))
                .collect(Collectors.toList());
    }
}