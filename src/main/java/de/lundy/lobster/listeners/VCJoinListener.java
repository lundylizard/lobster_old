package de.lundy.lobster.listeners;

import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.StatsManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VCJoinListener extends ListenerAdapter {

    private final StatsManager statsManager;
    public static List<Long> vcSizeChanged = new ArrayList<>();

    public VCJoinListener(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {

        if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {

            //People are paranoid a lobster is listening to their discord sessions
            event.getGuild().getAudioManager().setSelfDeafened(true);

            ChatUtils.print("VCUPDATE: (" + event.getGuild().getName() + ") Joined VC");
            statsManager.vcSession.put(event.getGuild().getIdLong(), System.currentTimeMillis() / 1000);

            // Adds another slot to the channel if it has a limit, so others can still join
            var userLimit = event.getChannelJoined().getUserLimit();

            if (userLimit != 0) {

                event.getChannelJoined().getManager().setUserLimit(userLimit + 1).queue();
                vcSizeChanged.add(event.getChannelJoined().getIdLong());

            }
        }
    }
}
