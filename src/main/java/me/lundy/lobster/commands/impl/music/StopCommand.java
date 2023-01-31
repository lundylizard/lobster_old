package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.player.stopTrack();
        event.reply("Stopped the playback.").queue();
    }

    @Override
    public String name() {
        return "stop";
    }

    @Override
    public String description() {
        return "Stop the music and clear the queue";
    }

}

