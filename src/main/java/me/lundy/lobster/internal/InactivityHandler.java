package me.lundy.lobster.internal;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.LoggerFactory;

public class InactivityHandler {

    public static void handleInactivity(Guild guild) {
        if (isGuildActive(guild)) {
            AudioManager audioManager = guild.getAudioManager();
            VoiceChannel connectedChannel = audioManager.getConnectedChannel().asVoiceChannel();

            if (areAllMembersDeafenedOrBots(connectedChannel)) {
                closeAudioConnection(guild);
                stopPlayback(guild);
                clearQueue(guild);
                logInactivity(guild);
            }
        }
    }

    private static boolean isGuildActive(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        return audioManager.isConnected();
    }

    private static boolean areAllMembersDeafenedOrBots(VoiceChannel voiceChannel) {
        for (Member member : voiceChannel.getMembers()) {
            if (!member.getVoiceState().isDeafened() && !member.getUser().isBot()) {
                return false;
            }
        }
        return true;
    }

    private static void closeAudioConnection(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
    }

    private static void stopPlayback(Guild guild) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioPlayer audioPlayer = musicManager.audioPlayer;
        audioPlayer.stopTrack();
    }

    private static void clearQueue(Guild guild) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        musicManager.scheduler.queue.clear();
    }

    private static void logInactivity(Guild guild) {
        LoggerFactory.getLogger(InactivityHandler.class).info("Left voice chat in guild {} due to inactivity.", guild.getName());
    }

}
