package me.lundy.lobster.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildLeaveListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(GuildLeaveListener.class);

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        AudioPlayer audioPlayer = musicManager.audioPlayer;
        audioPlayer.stopTrack();
        musicManager.scheduler.queue.clear();
        logger.info("- Left guild {} ({})", event.getGuild().getName(), event.getJDA().getGuilds().size());
    }
}
