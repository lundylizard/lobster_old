package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobsterbot;
import de.lundy.lobster.utils.ChatUtils;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VCJoinListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {

        //People are paranoid a lobster is listening to their discord sessions
        event.getGuild().getAudioManager().setSelfDeafened(true);
        if (Lobsterbot.DEBUG) ChatUtils.print("Joined VC in " + event.getGuild().getName());

    }
}
