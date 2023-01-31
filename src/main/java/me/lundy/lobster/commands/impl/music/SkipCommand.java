package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SkipCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        event.reply(String.format("Skipping `%s`...", musicManager.audioPlayer.getPlayingTrack().getInfo().title)).queue();
        musicManager.scheduler.nextTrack();
    }

    @Override
    public String name() {
        return "skip";
    }

    @Override
    public String description() {
        return "Skip the current song";
    }

}


