package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class VolumeCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        OptionMapping volumeOption = event.getOption("amount");

        if (volumeOption == null) {
            int currentVolume = musicManager.audioPlayer.getVolume();
            event.replyFormat("Current volume is %d%%.", currentVolume).setEphemeral(true).queue();
            return;
        }

        int newVolume = volumeOption.getAsInt();
        if (newVolume <= 10) {
            event.reply("Volume cannot be 10 or lower.").setEphemeral(true).queue();
            return;
        }

        boolean probablyLoud = newVolume >= 101;
        musicManager.audioPlayer.setVolume(newVolume);

        String message = probablyLoud ? " - This might be loud." : "";
        event.replyFormat("Changed the volume to %d%%%s", newVolume, message).queue();

    }

    @Override
    public String name() {
        return "volume";
    }

    @Override
    public String description() {
        return "Change the volume";
    }

    @Override
    public List<OptionData> options() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "Amount of volume"));
    }

}
