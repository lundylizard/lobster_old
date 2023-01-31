package me.lundy.lobster.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

public class InactivityManager {

    private static InactivityManager instance;

    public static InactivityManager getInstance() {
        if (instance == null) instance = new InactivityManager();
        return instance;
    }

    public void handleInactivity(ShardManager shardManager) {

        for (Guild g : shardManager.getGuilds()) {

            if (g.getAudioManager().isConnected()) {

                GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(g);
                AudioPlayer audioPlayer = musicManager.audioPlayer;

                if (g.getAudioManager().getConnectedChannel().getMembers()
                        .stream().noneMatch(member -> member.getVoiceState().isDeafened() && !member.getUser().isBot())) {
                    g.getAudioManager().closeAudioConnection();
                    audioPlayer.stopTrack();
                    musicManager.scheduler.queue.clear();
                }

            }
        }
    }
}
