package me.lundy.lobster.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.*;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.checks.RunCheck;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.BotUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@CommandInfo(name = "seek", description = "Change the time of the current song")
public class SeekCommand extends Command implements CommandOptions {

    @Override
    @RunCheck(check = CommandCheck.IN_SAME_VOICE)
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
    public List<OptionData> options() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "Amount to seek (seconds)", true));
    }
}