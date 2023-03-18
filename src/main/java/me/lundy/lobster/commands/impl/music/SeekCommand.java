package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        if (track == null) {
            event.reply(":warning: There is currently no song playing.").setEphemeral(true).queue();
            return;
        }

        int seekAmount = event.getOption("amount").getAsInt();
        long newPos = track.getPosition() + (seekAmount * 1000L);
        newPos = Math.max(0L, newPos);

        if (newPos >= track.getDuration()) {
            musicManager.scheduler.nextTrack();
            event.reply(String.format("Skipped `%s`", track.getInfo().title)).queue();
        } else {
            track.setPosition(newPos);
            String position = BotUtils.formatTime(newPos);
            event.reply(String.format("Set song position to **%s**", position)).queue();
        }
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