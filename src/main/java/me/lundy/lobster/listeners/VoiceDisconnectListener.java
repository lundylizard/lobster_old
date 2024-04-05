package me.lundy.lobster.listeners;

import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceDisconnectListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getEntity().getUser().equals(event.getJDA().getSelfUser())) {
            if (event.getNewValue() == null) {
                var musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
                musicManager.audioPlayer.setPaused(false);
                musicManager.scheduler.queue.clear();
                musicManager.scheduler.player.stopTrack();
            }
        }
    }

}
