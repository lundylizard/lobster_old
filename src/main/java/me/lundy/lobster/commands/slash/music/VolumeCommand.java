package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.*;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@CommandInfo(name = "volume", description = "Change the volume")
public class VolumeCommand extends Command implements CommandOptions {

    @Override
    @RunCheck(check = CommandCheck.IN_SAME_VOICE)
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        OptionMapping volumeOption = event.getOption("amount");

        if (volumeOption == null) {
            int currentVolume = musicManager.audioPlayer.getVolume();
            event.reply("The current volume is `" + currentVolume + "%`").queue();
            return;
        }

        int newVolume = volumeOption.getAsInt();
        if (newVolume < 10) {
            event.reply("Volume cannot be lower than `10%`").setEphemeral(true).queue();
            return;
        }

        boolean overLimit = newVolume >= 101;

        String message = "Changed the volume to `" + newVolume + "%`";

        if (overLimit) {
            message += "\n> A volume above 100 might worsen the quality.";
        }

        musicManager.audioPlayer.setVolume(newVolume);
        event.reply(message).queue();
    }

    @Override
    public List<OptionData> options() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "Amount of volume"));
    }

}
