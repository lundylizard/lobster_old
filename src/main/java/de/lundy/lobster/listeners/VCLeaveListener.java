package de.lundy.lobster.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VCLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {

        if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {

            // When bot leaves vc lower the user limit again
            if (VCJoinListener.vcSizeChanged.contains(event.getChannelLeft().getIdLong())) {
                System.out.printf("Changing Voice Channel (%s) limit from {%d} to {%d}%n", event.getChannelLeft().getName(), event.getChannelLeft().getUserLimit(), event.getChannelLeft().getUserLimit() - 1);
                event.getChannelLeft().getManager().setUserLimit(event.getChannelLeft().getUserLimit() - 1).queue();
                VCJoinListener.vcSizeChanged.remove(event.getChannelLeft().getIdLong());
            }

            System.out.printf("(%s) Left Voice Channel (%s)%n", event.getGuild().getName(), event.getChannelLeft().getName());

        }

    }
}
