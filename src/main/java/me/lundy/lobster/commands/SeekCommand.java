package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@CommandInfo(name = "seek", description = "Change the time of the current song")
public class SeekCommand extends Command implements CommandOptions {

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

        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        if (track == null) {
            context.getEvent().reply(":warning: There is currently no song playing").setEphemeral(true).queue();
            return;
        }

        int seekAmount = context.getEvent().getOption("amount").getAsInt();
        long newPos = track.getPosition() + (seekAmount * 1000L);
        newPos = Math.max(0L, newPos);

        if (newPos >= track.getDuration()) {
            musicManager.scheduler.nextTrack();
            context.getEvent().replyFormat("Skipped `%s`", track.getInfo().title).queue();
        } else {
            track.setPosition(newPos);
            context.getEvent().replyFormat("Set song position to **%s**", StringUtils.formatTime(newPos)).queue();
        }
    }

    @Override
    public List<OptionData> options() {
        return List.of(new OptionData(OptionType.INTEGER, "amount", "Amount to seek (seconds)", true));
    }
}