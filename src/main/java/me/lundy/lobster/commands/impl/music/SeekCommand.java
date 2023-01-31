package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class SeekCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        OptionMapping seekOption = event.getOption("amount");
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (musicManager.audioPlayer.getPlayingTrack() == null) {
            event.reply(":warning: There is currently no song playing.").setEphemeral(true).queue();
            return;
        }

        int seekAmount = seekOption.getAsInt() * 1000;
        long newPos = musicManager.audioPlayer.getPlayingTrack().getPosition() + seekAmount;

        if (newPos < 0) newPos = 0L;

        if (newPos > musicManager.audioPlayer.getPlayingTrack().getDuration()) {
            musicManager.scheduler.nextTrack();
            event.reply(String.format("Skipped `%s`", musicManager.audioPlayer.getPlayingTrack().getInfo().title)).queue();
            return;
        }

        musicManager.audioPlayer.getPlayingTrack().setPosition(newPos);
        String position = BotUtils.formatTime(musicManager.audioPlayer.getPlayingTrack().getPosition());
        event.reply(String.format("Set song position to **%s**", position)).queue();

    }

    @Override
    public String name() {
        return "seek";
    }

    @Override
    public String description() {
        return "Change the position of the current song";
    }

    @Override
    public List<OptionData> options() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "Amount to seek (seconds)", true));
    }
}