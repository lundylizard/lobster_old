package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandInfo(name = "loop", description = "Change whether the current song should loop")
public class LoopCommand extends Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        boolean isRepeating = musicManager.scheduler.isRepeating();
        musicManager.scheduler.setRepeating(!isRepeating);
        event.reply(String.format("%s looping current song.", musicManager.scheduler.isRepeating() ? "Now" : "No longer")).queue();
    }

}
