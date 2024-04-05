package me.lundy.lobster.commands;

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

public class RemoveCommand extends BotCommand {

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

        if (musicManager.scheduler.queue.isEmpty()) {
            event.reply(Reply.QUEUE_EMPTY.getMessage()).queue();
            return;
        }

        int index = event.getOption("index").getAsInt();
        var trackList = new ArrayList<>(musicManager.scheduler.queue);

        if (trackList.get(index) == null) {
            event.reply(Reply.TRACK_NOT_IN_QUEUE.getMessage()).setEphemeral(true).queue();
            return;
        }

        var trackToBeDeleted = trackList.get(index);
        musicManager.scheduler.queue.removeFirstOccurrence(trackToBeDeleted);
        musicManager.scheduler.invalidatePagination();
        event.replyFormat(Reply.REMOVED_SONG.getMessage(), trackToBeDeleted.getInfo().author, trackToBeDeleted.getInfo().title).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        var optionIndex = new OptionData(OptionType.INTEGER, "index", "What song should be removed", true, true);
        return Commands.slash("remove", "Remove a song from the queue").addOptions(optionIndex);
    }

    @Override
    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        var guild = event.getGuild();
        if (guild == null) return null;
        var musicManager = PlayerManager.getInstance().getMusicManager(guild);
        var trackList = new ArrayList<>(musicManager.scheduler.queue);
        return trackList.stream()
                .filter(audioTrack -> ((audioTrack.getInfo().author + " - " + audioTrack.getInfo().title)).toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                .limit(25)
                .map(audioTrack -> new Command.Choice((audioTrack.getInfo().author + " - " + audioTrack.getInfo().title), trackList.indexOf(audioTrack)))
                .sorted(Comparator.comparingInt(a -> a.getName().charAt(0)))
                .collect(Collectors.toList());
    }
}
