package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
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

        int from = context.getEvent().getOption("song").getAsInt();
        int to = context.getEvent().getOption("to").getAsInt();

        if (from <= 0 || to <= 0) {
            context.getEvent().reply(Reply.MOVE_POSITION_INVALID.getMessage()).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        from = Math.min(from, musicManager.scheduler.queue.size());
        to = Math.min(to, musicManager.scheduler.queue.size());
        List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
        AudioTrack movedTrack = trackList.get(from - 1);
        trackList.add(to - 1, trackList.get(from - 1));
        trackList.remove(from);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);
        context.getEvent().replyFormat(Reply.MOVE_SUCCESSFUL.getMessage(), movedTrack.getInfo().title, to).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionTo = new OptionData(OptionType.INTEGER, "to", "Where the song should be moved to", true);
        OptionData optionFrom = new OptionData(OptionType.INTEGER, "song", "What song you want to move", true, true);
        return Commands.slash("move", "Move songs in the queue").addOptions(optionTo, optionFrom);
    }

    @Override
    public List<net.dv8tion.jda.api.interactions.commands.Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return null;
        if (event.getFocusedOption().getName().equals("song")) {
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
            List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
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
