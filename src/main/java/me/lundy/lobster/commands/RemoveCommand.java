package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
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

        if (musicManager.scheduler.queue.isEmpty()) {
            context.getEvent().reply(Reply.QUEUE_EMPTY.getMessage()).queue();
            return;
        }

        int index = context.getEvent().getOption("index").getAsInt();
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);

        if (trackList.get(index) == null) {
            context.getEvent().reply(Reply.TRACK_NOT_IN_QUEUE.getMessage()).setEphemeral(true).queue();
            return;
        }

        AudioTrack trackToBeDeleted = trackList.get(index);
        musicManager.scheduler.queue.removeFirstOccurrence(trackToBeDeleted);
        context.getEvent().replyFormat(Reply.REMOVED_SONG.getMessage(), trackToBeDeleted.getInfo().author, trackToBeDeleted.getInfo().title).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionIndex = new OptionData(OptionType.INTEGER, "index", "What song should be removed", true, true);
        return Commands.slash("remove", "Remove a song from the queue").addOptions(optionIndex);
    }

    @Override
    public List<net.dv8tion.jda.api.interactions.commands.Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return null;
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        return trackList.stream()
                .filter(audioTrack -> (audioTrack.getInfo().author + " - " + audioTrack.getInfo().title).toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                .limit(25)
                .map(audioTrack -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(audioTrack.getInfo().author + " - " + audioTrack.getInfo().title, trackList.indexOf(audioTrack)))
                .sorted(Comparator.comparingInt(a -> a.getName().charAt(0)))
                .collect(Collectors.toList());
    }
}
