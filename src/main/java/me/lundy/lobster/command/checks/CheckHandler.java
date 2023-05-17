package me.lundy.lobster.command.checks;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class CheckHandler {

    public static boolean runCheck(CommandCheck commandCheck, @NotNull Member self, @NotNull Member executor) {
        if (self.getVoiceState() == null || self.getVoiceState().getChannel() == null) return false;
        if (executor.getVoiceState() == null || executor.getVoiceState().getChannel() == null) return false;

        VoiceChannel selfVoiceChannel = self.getVoiceState().getChannel().asVoiceChannel();
        VoiceChannel memberVoiceChannel = executor.getVoiceState().getChannel().asVoiceChannel();

        if (commandCheck == CommandCheck.SELF_IN_VOICE) {
            return self.getVoiceState().inAudioChannel();
        }
        if (commandCheck == CommandCheck.IN_SAME_VOICE) {
            return selfVoiceChannel != null && memberVoiceChannel != null && selfVoiceChannel.getIdLong() == memberVoiceChannel.getIdLong();
        }
        if (commandCheck == CommandCheck.EXECUTOR_IN_VOICE) {
            return executor.getVoiceState().inAudioChannel();
        }
        return false;
    }

}
