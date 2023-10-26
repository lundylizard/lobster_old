package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinCommand extends BotCommand {

    private final Logger logger = LoggerFactory.getLogger(JoinCommand.class);

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(Reply.EXECUTOR_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        if (context.selfInVoice()) {
            context.getEvent().reply(Reply.SELF_ALREADY_IN_VOICE.getMessage()).setEphemeral(true).queue();
            return;
        }

        AudioManager audioManager = context.getGuild().getAudioManager();
        AudioChannel audioChannel = context.getExecutorVoiceState().getChannel();

        if (audioChannel == null) {
            context.getEvent().reply(Reply.ERROR_VOICE_CHANNEL_NULL.getMessage()).setEphemeral(true).queue();
            return;
        }

        try {
            audioManager.openAudioConnection(audioChannel);
        } catch (InsufficientPermissionException e) {
            context.getEvent().reply(Reply.ERROR_NO_PERMISSIONS_VOICE.getMessage()).setEphemeral(true).queue();
        } finally {
            context.getEvent().replyFormat(Reply.JOINED_VOICE.getMessage(), audioChannel.getName()).queue();
        }

        // try {
        //     SettingsManager settingsManager = Lobster.getInstance().getSettingsManager();
        //     GuildSettings guildSettings = settingsManager.getSettings(context.getGuild().getIdLong());
        //     int volume = guildSettings.getVolume();
        //     GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        //     musicManager.audioPlayer.setVolume(volume);
        // } catch (SQLException e) {
        //     logger.error("Could not set volume on startup", e);
        // }

    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("join", "Make lobster join your voice channel");
    }
}
