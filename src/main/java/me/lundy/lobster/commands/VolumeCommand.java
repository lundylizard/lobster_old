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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VolumeCommand extends BotCommand {

    private static final int VOLUME_MAX = 100;
    private static final int VOLUME_MIN = 10;

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
        var volumeOption = event.getOption("amount");

        if (volumeOption == null) {
            event.replyFormat(Reply.CURRENT_VOLUME.getMessage(), musicManager.audioPlayer.getVolume()).queue();
            return;
        }

        int newVolume = volumeOption.getAsInt();
        if (newVolume < VOLUME_MIN) {
            event.replyFormat(Reply.VOLUME_LOWER.getMessage(), VOLUME_MIN).queue();
            return;
        }

        if (newVolume > VOLUME_MAX) {
            event.replyFormat(Reply.VOLUME_HIGHER.getMessage(), VOLUME_MAX).queue();
            return;
        }

        musicManager.audioPlayer.setVolume(newVolume);
        event.replyFormat(Reply.VOLUME_CHANGED.getMessage(), newVolume).queue();
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
