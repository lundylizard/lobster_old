package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class LoopCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
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
        boolean isRepeating = musicManager.scheduler.isRepeating();
        musicManager.scheduler.setRepeating(!isRepeating);
        String newState = musicManager.scheduler.isRepeating() ? "Now" : "No longer";
        context.getEvent().replyFormat(Reply.LOOP_STATE.getMessage(), newState).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("loop", "Toggle looping of the current song");
    }

}
