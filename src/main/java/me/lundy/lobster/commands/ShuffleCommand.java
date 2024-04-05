package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.CommandHelper;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Collections;

public class ShuffleCommand extends BotCommand {

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
        var trackList = new ArrayList<>(musicManager.scheduler.queue);
        Collections.shuffle(trackList);
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.queue.addAll(trackList);
        musicManager.scheduler.invalidatePagination();
        event.reply(Reply.QUEUE_SHUFFLED.getMessage()).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("shuffle", "Shuffle the queue");
    }
}
