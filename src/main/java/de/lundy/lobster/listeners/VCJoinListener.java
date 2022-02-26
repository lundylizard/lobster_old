package de.lundy.lobster.listeners;

import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.StatsManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VCJoinListener extends ListenerAdapter {

    private final StatsManager statsManager;

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

        }

    }
}
