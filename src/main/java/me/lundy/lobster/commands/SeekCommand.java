package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
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
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();

        if (track == null) {
            context.getEvent().reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
            return;
        }

        String newTime = context.getEvent().getOption("position").getAsString();

        if (!StringUtils.isValidTimeFormat(newTime)) {
            context.getEvent().reply(Reply.INVALID_TIME_FORMAT.getMessage()).queue();
            return;
        }

        int newSeconds = StringUtils.timeToSeconds(newTime);
        long newPos = newSeconds * 1000L;
        newPos = Math.max(0L, newPos);

        if (newPos >= track.getDuration()) {
            musicManager.scheduler.nextTrack();
            context.getEvent().replyFormat(Reply.TRACK_SKIPPED.getMessage(), track.getInfo().title, track.getInfo().author).queue();
        } else {
            track.setPosition(newPos);
            context.getEvent().replyFormat(Reply.TRACK_SET_POSITION.getMessage(), StringUtils.formatTime(newPos)).queue();
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionAmount = new OptionData(OptionType.STRING, "position", "New time of song", true, true);
        return Commands.slash("seek", "Change the time of the current song").addOptions(optionAmount);
    }

    @Override
    public List<net.dv8tion.jda.api.interactions.commands.Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        if (track == null) return Collections.emptyList();
        long max = track.getDuration();
        return Stream.of(StringUtils.getAvailableTimes(max))
                .limit(25)
                .map(time -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(time, time))
                .collect(Collectors.toList());
    }
}