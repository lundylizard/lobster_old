package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobsterbot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VCLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {

        if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {

            // When bot leaves vc lower the user limit again
            if (VCJoinListener.vcSizeChanged.contains(event.getChannelLeft().getIdLong())) {
                Lobsterbot.LOGGER.info("Changing Voice Channel ({}) limit from {} to {}", event.getChannelLeft().getName(), event.getChannelLeft().getUserLimit(), event.getChannelLeft().getUserLimit() - 1);
                event.getChannelLeft().getManager().setUserLimit(event.getChannelLeft().getUserLimit() - 1).queue();
                VCJoinListener.vcSizeChanged.remove(event.getChannelLeft().getIdLong());
            }

            Lobsterbot.LOGGER.info("({}) Left Voice Channel ({})", event.getGuild().getName(), event.getChannelLeft().getName());

        }

    }
}
