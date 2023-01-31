package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LoopCommand extends BotCommand {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        boolean isRepeating = musicManager.scheduler.isRepeating();
        musicManager.scheduler.setRepeating(!isRepeating);
        event.reply(String.format("%s looping current song.", isRepeating ? "Now" : "No longer")).queue();
    }

    @Override
    public String name() {
        return "loop";
    }

    @Override
    public String description() {
        return "Change whether the current song should loop";
    }

}
