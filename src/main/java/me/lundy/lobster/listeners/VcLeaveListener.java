package me.lundy.lobster.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VcLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

        if (event.getEntity().getUser().equals(event.getJDA().getSelfUser())) {

        }

    }
}
