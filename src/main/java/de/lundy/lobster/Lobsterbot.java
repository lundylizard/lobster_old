package de.lundy.lobster;

import de.lundy.lobster.commands.admin.AdminCommand;
import de.lundy.lobster.commands.impl.CommandHandler;
import de.lundy.lobster.commands.misc.HelpCommand;
import de.lundy.lobster.commands.misc.InviteCommand;
import de.lundy.lobster.commands.misc.PrefixCommand;
import de.lundy.lobster.commands.music.*;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.listeners.*;
import de.lundy.lobster.utils.MySQLUtils;
import de.lundy.lobster.utils.mysql.BlacklistManager;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lobsterbot {

    // Please note: The Secrets class is not publicly available, because I did not intend this to be built from others.
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static final Logger LOGGER = LoggerFactory.getLogger(Lobsterbot.class);
    public static final boolean DEBUG = false;

    public static void main(String @NotNull [] args) {

        var jdaBuilder = JDABuilder.create(DEBUG ? Secrets.DEBUG_DISCORD_TOKEN : args[0], EnumSet.allOf(GatewayIntent.class));

        var database = new MySQLUtils();
        var settingsManager = new SettingsManager(database);
        var blacklistManager = new BlacklistManager(database);

        jdaBuilder.addEventListeners(new MessageCommandListener(settingsManager, blacklistManager));
        jdaBuilder.addEventListeners(new ReadyListener());
        jdaBuilder.addEventListeners(new JoinListener(settingsManager));
        jdaBuilder.addEventListeners(new VCJoinListener());
        jdaBuilder.addEventListeners(new VCLeaveListener());

        // Register commands, I know there's prettier ways to do this.
        CommandHandler.addCommand(new String[]{"join"}, new JoinCommand());
        CommandHandler.addCommand(new String[]{"play", "p", "sr"}, new PlayCommand());
        CommandHandler.addCommand(new String[]{"nowplaying", "np"}, new NowPlayingCommand());
        CommandHandler.addCommand(new String[]{"skip", "s"}, new SkipCommand());
        CommandHandler.addCommand(new String[]{"stop"}, new StopCommand());
        CommandHandler.addCommand(new String[]{"queue", "q"}, new QueueCommand(settingsManager));
        CommandHandler.addCommand(new String[]{"disconnect", "leave", "dc"}, new LeaveCommand());
        CommandHandler.addCommand(new String[]{"remove", "rm"}, new RemoveCommand());
        CommandHandler.addCommand(new String[]{"shuffle"}, new ShuffleCommand());
        CommandHandler.addCommand(new String[]{"loop", "repeat"}, new LoopCommand());
        CommandHandler.addCommand(new String[]{"link", "url"}, new LinkCommand());
        CommandHandler.addCommand(new String[]{"help"}, new HelpCommand(settingsManager));
        CommandHandler.addCommand(new String[]{"seek"}, new SeekCommand());
        CommandHandler.addCommand(new String[]{"move", "mv"}, new MoveCommand());
        CommandHandler.addCommand(new String[]{"pause"}, new PauseCommand());
        CommandHandler.addCommand(new String[]{"resume", "unpause"}, new ResumeCommand());
        CommandHandler.addCommand(new String[]{"prefix"}, new PrefixCommand(settingsManager));
        CommandHandler.addCommand(new String[]{"invite"}, new InviteCommand(settingsManager));
        CommandHandler.addCommand(new String[]{"admin"}, new AdminCommand(blacklistManager));

        JDA jda = null;

        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        tick(jda); // This gets executed every 60 seconds

    }

    private static void tick(JDA jda) {

        scheduler.scheduleWithFixedDelay(() -> {

            var serverCount = jda.getGuilds().size();

            // Update activity to show how many servers this bot is on
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("on " + serverCount + " servers."));

            for (var guilds : jda.getGuilds()) {

                // Checks if lobster is connected to a vc
                if (guilds.getAudioManager().isConnected()) {

                    var musicManager = PlayerManager.getInstance().getMusicManager(guilds);
                    var audioPlayer = musicManager.audioPlayer;

                    // If there is no other undeafened member or bot in vc stop playing music and leave vc
                    if (Objects.requireNonNull(guilds.getAudioManager().getConnectedChannel()).getMembers().stream()
                            .noneMatch(x -> !Objects.requireNonNull(x.getVoiceState()).isDeafened() && !x.getUser().isBot())) {

                        guilds.getAudioManager().closeAudioConnection();
                        audioPlayer.stopTrack();
                        musicManager.scheduler.queue.clear();
                        LOGGER.info("Left Voice Channel in {} ({}) due to inactivity.", guilds.getName(), guilds.getAudioManager().getConnectedChannel().getName());

                    }
                }
            }

        }, 0, 60, TimeUnit.SECONDS);

    }

}
