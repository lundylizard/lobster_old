package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VolumeCommand extends BotCommand {

    private static final int VOLUME_MAX = 100;
    private static final int VOLUME_MIN = 10;

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
        OptionMapping volumeOption = context.getEvent().getOption("amount");

        if (volumeOption == null) {
            context.getEvent().replyFormat(Reply.CURRENT_VOLUME.getMessage(), musicManager.audioPlayer.getVolume()).queue();
            return;
        }

        int newVolume = volumeOption.getAsInt();
        if (newVolume < VOLUME_MIN) {
            context.getEvent().replyFormat(Reply.VOLUME_LOWER.getMessage(), VOLUME_MIN).queue();
            return;
        }

        if (newVolume > VOLUME_MAX) {
            context.getEvent().replyFormat(Reply.VOLUME_HIGHER.getMessage(), VOLUME_MAX).queue();
            return;
        }

        musicManager.audioPlayer.setVolume(newVolume);
        context.getEvent().replyFormat(Reply.VOLUME_CHANGED.getMessage(), newVolume).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionAmount = new OptionData(OptionType.INTEGER, "amount", "Amount of volume", false, true);
        return Commands.slash("volume", "Change the volume").addOptions(optionAmount);
    }

    @Override
    public List<Command.Choice> onAutocomplete(CommandAutoCompleteInteractionEvent event) {
        return IntStream.of(10, 25, 50, 75, 100)
                .mapToObj(choice -> new Command.Choice(String.valueOf(choice), choice))
                .collect(Collectors.toList());
    }
}
