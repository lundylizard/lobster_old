package me.lundy.lobster.commands;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@CommandInfo(name = "volume", description = "Change the volume")
public class VolumeCommand extends Command implements CommandOptions {

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(":warning: You are not in a voice channel").setEphemeral(true).queue();
            return;
        }

        if (!context.selfInVoice()) {
            context.getEvent().reply(":warning: I am not in a voice channel").setEphemeral(true).queue();
            return;
        }

        if (!context.inSameVoice()) {
            context.getEvent().reply(":warning: We are not in the same voice channel").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        OptionMapping volumeOption = context.getEvent().getOption("amount");

        if (volumeOption == null) {
            int currentVolume = musicManager.audioPlayer.getVolume();
            context.getEvent().replyFormat("The current volume is `%d%`", currentVolume).queue();
            return;
        }

        int newVolume = volumeOption.getAsInt();

        if (newVolume < 10) {
            context.getEvent().reply("Volume cannot be lower than `10%`").queue();
            return;
        }

        if (newVolume > 100) {
            context.getEvent().reply("Volume cannot be higher than `100%`").queue();
            return;
        }

        musicManager.audioPlayer.setVolume(newVolume);
        context.getEvent().replyFormat("Changed volume to `%d%`", newVolume).queue();
    }

    @Override
    public List<OptionData> options() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "Amount of volume"));
    }

}
