package de.lundy.lobster;

import de.lundy.lobster.commands.impl.CommandHandler;
import de.lundy.lobster.commands.music.*;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.listeners.MessageCommandListener;
import de.lundy.lobster.listeners.ReadyListener;
import de.lundy.lobster.utils.ChatUtils;
import de.lundy.lobster.utils.mysql.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A discord bot to play music, officially supports youtube, spotify and mp3/mp4-files.
 * Everything is handled by LavaPlayer, so for more information check that out.
 * Shoutout to the Lobster Gang, where the bot name originated from and this bot originally was planned on being used.
 * I had big plans for this, but I wanted to focus on something else, so I decided to make this public instead.
 *
 * @author lundylizard
 */
public class Lobsterbot {

    // Please note: The Secrets class is not publicly available, because I did not intend this to be built from others.
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
    private static SettingsManager settingsManager;

    public static void main(String @NotNull [] args) throws SQLException, LoginException {

        var jdaBuilder = JDABuilder.create(args[0], EnumSet.allOf(GatewayIntent.class));
        settingsManager = new SettingsManager();
        settingsManager.generateSettingsDatabase();
        jdaBuilder.addEventListeners(new MessageCommandListener(settingsManager));
        jdaBuilder.addEventListeners(new ReadyListener());

        //Register commands, I know there's prettier ways to do this.
        CommandHandler.addCommand(new String[]{"join"}, new JoinCommand());
        CommandHandler.addCommand(new String[]{"play", "p", "sr"}, new PlayCommand());
        CommandHandler.addCommand(new String[]{"nowplaying", "np"}, new NowPlayingCommand());
        CommandHandler.addCommand(new String[]{"skip", "s"}, new SkipCommand());
        CommandHandler.addCommand(new String[]{"stop"}, new StopCommand());
        CommandHandler.addCommand(new String[]{"queue", "q"}, new QueueCommand());
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

        var jda = jdaBuilder.build();
        tick(jda); //This gets executed every 10 seconds

    }

    private static void tick(JDA jda) {

        scheduler.scheduleWithFixedDelay(() -> {

            for (var guilds : jda.getGuilds()) {

                //Update activity to show how many servers this bot is on
                jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("on " + jda.getGuilds().size() + " servers."));

                try {

                    //Create a new entry in the settings table if the server does not exist in there
                    if (!settingsManager.serverInDatabase(guilds.getIdLong())) {
                        ChatUtils.print("DATABASE: " + guilds.getName() + " is not in the database yet. Creating...");
                        settingsManager.putServerIntoDatabase(guilds.getIdLong(), "!");
                    }

                    //Checks if Lobster is connected to a vc
                    if (guilds.getAudioManager().isConnected()) {

                        var musicManager = PlayerManager.getInstance().getMusicManager(guilds);
                        var audioPlayer = musicManager.audioPlayer;

                        //If there is no other undeafened member or bot in vc stop playing music
                        if (Objects.requireNonNull(guilds.getAudioManager().getConnectedChannel()).getMembers().stream()
                                .noneMatch(x -> !Objects.requireNonNull(x.getVoiceState()).isDeafened() && !x.getUser().isBot())) {

                            guilds.getAudioManager().closeAudioConnection();
                            audioPlayer.stopTrack();
                            musicManager.scheduler.queue.clear();

                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }, 0, 10, TimeUnit.SECONDS);

    }
}
