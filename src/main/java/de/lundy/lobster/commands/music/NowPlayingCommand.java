package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.utils.BotUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record NowPlayingCommand(SettingsManager settingsManager) implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        if (! (selfVoiceState != null && selfVoiceState.inVoiceChannel())) {
            channel.sendMessage(":warning: I am not playing anything.").queue();
            return;
        }

        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        if (! (memberVoiceState != null && memberVoiceState.inVoiceChannel())) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        if (!Objects.equals(memberVoiceState.getChannel(), selfVoiceState.getChannel())) {
            channel.sendMessage(":warning: You need to be in the same voice channel as me.").queue();
            return;
        }

        var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        var audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage(":warning: There is currently no track playing.").queue();
            return;
        }

        var track = audioPlayer.getPlayingTrack();
        var trackInfo = track.getInfo();

        var nowPlaying = new StringBuilder();
        nowPlaying.append(":notes: **NOW PLAYING:** ").append(trackInfo.title).append("\n`by ").append(trackInfo.author).append("` [`").append(BotUtils.getTrackPosition(track.getPosition(), track.getDuration())).append("`]");
        channel.sendMessage(new EmbedBuilder().setDescription(nowPlaying).setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor()).setFooter(BotUtils.randomFooter(settingsManager.getPrefix(event.getGuild().getIdLong()))).build()).queue();

    }
}
