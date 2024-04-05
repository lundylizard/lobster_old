package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MoveCommand extends BotCommand {

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

        int from = event.getOption("song").getAsInt();
        int to = event.getOption("to").getAsInt();

        if (from <= 0 || to <= 0) {
            event.reply(Reply.MOVE_POSITION_INVALID.getMessage()).setEphemeral(true).queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        from = Math.min(from, musicManager.scheduler.queue.size());
        to = Math.min(to, musicManager.scheduler.queue.size());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        AudioTrack movedTrack = trackList.get(from - 1);
        trackList.add(to - 1, trackList.get(from - 1));
        trackList.remove(from);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);
        event.replyFormat(Reply.MOVE_SUCCESSFUL.getMessage(), movedTrack.getInfo().title, to).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        var optionTo = new OptionData(OptionType.INTEGER, "to", "Where the song should be moved to", true);
        var optionFrom = new OptionData(OptionType.INTEGER, "song", "What song you want to move", true, true);
        return Commands.slash("move", "Move songs in the queue").addOptions(optionTo, optionFrom);
    }

    @Override
    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        var guild = event.getGuild();
        if (guild == null) return null;
        if (event.getFocusedOption().getName().equals("song")) {
            var musicManager = PlayerManager.getInstance().getMusicManager(guild);
            var trackList = new ArrayList<>(musicManager.scheduler.queue);
            return trackList.stream()
                    .filter(audioTrack -> (audioTrack.getInfo().author + " - " + audioTrack.getInfo().title).toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .limit(25)
                    .map(audioTrack -> new Command.Choice(audioTrack.getInfo().author + " - " + audioTrack.getInfo().title, trackList.indexOf(audioTrack) + 1))
                    .sorted(Comparator.comparingInt(a -> a.getName().charAt(0)))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
