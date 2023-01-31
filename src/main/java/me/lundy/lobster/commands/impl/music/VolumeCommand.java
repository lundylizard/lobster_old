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

        OptionMapping volumeOption = event.getOption("amount");
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (volumeOption == null) {
            event.reply(String.format("Current volume is %d", musicManager.audioPlayer.getVolume())).setEphemeral(true).queue();
            return;
        }

        int oldVolume = musicManager.audioPlayer.getVolume();
        int newVolume = volumeOption.getAsInt();

        if (newVolume <= 0) {
            event.reply("Volume cannot be 0 or lower.").setEphemeral(true).queue();
            return;
        }

        boolean probablyLoud = newVolume >= 101;
        musicManager.audioPlayer.setVolume(newVolume);
        event.reply(String.format("Changed the volume to %d%s", newVolume, probablyLoud ? "% - This might be loud." : "%")).queue();
        getLogger().debug("Volume Command: oldValue={} newValue={}", newVolume, oldVolume);

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
