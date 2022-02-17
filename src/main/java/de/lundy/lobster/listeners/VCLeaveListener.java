package de.lundy.lobster.listeners;

import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.StatsManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class VCLeaveListener extends ListenerAdapter {

    private StatsManager statsManager;

    public VCLeaveListener(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {

        var serverId = event.getGuild().getIdLong();

        if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {

            var timePlayed = statsManager.calculateTimePlayed(serverId); // putting it in a var for consistency

            ChatUtils.print("VCUPDATE: (" + event.getGuild().getName() + ") Left VC after " + timePlayed + " seconds.");

            try {

                statsManager.setTimePlayed(serverId, statsManager.getTimePlayed(serverId) + timePlayed);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            statsManager.vcSession.remove(serverId); // reset vc time counter

        }

    }
}
