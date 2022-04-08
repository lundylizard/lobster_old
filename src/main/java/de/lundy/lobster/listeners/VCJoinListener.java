package de.lundy.lobster.listeners;

import de.lundy.lobster.Lobsterbot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VCJoinListener extends ListenerAdapter {

    protected static final List<Long> vcSizeChanged = new ArrayList<>();

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {

        if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {

            //People are paranoid a lobster is listening to their discord sessions
            event.getGuild().getAudioManager().setSelfDeafened(true);
            Lobsterbot.LOGGER.info("Joined Voice Channel in {} ({})", event.getGuild().getName(), event.getChannelJoined().getName());

            // Adds another slot to the channel if it has a limit, so others can still join
            var userLimit = event.getChannelJoined().getUserLimit();

            if (userLimit != 0) {
                Lobsterbot.LOGGER.info("Changing Voice Channel ({}) limit from {} to {}", event.getChannelJoined().getName(), userLimit, userLimit + 1);
                event.getChannelJoined().getManager().setUserLimit(userLimit + 1).queue();
                vcSizeChanged.add(event.getChannelJoined().getIdLong());
            }

        }
    }
}
