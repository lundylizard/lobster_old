package me.lundy.lobster.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Objects;

public class VoiceChatCheck {

    public static CheckResult runCheck(InteractionHook interactionHook) {

        Member executor = interactionHook.getInteraction().getMember();
        Member self = interactionHook.getInteraction().getGuild().getSelfMember();

        if (!executor.getVoiceState().inAudioChannel()) {
            return CheckResult.EXECUTOR_NOT_IN_VOICE;
        }

        if (self.getVoiceState().inAudioChannel()) {
            return CheckResult.SELF_NOT_IN_VOICE;
        }

        if (!Objects.equals(self.getVoiceState().getChannel(), executor.getVoiceState().getChannel())) {
            return CheckResult.NOT_IN_SAME_VOICE;
        }

        return CheckResult.PASSED;

    }

    public enum CheckResult {

        SELF_NOT_IN_VOICE(":warning: I am not in a voice channel."),
        EXECUTOR_NOT_IN_VOICE(":warning: You are not in a voice channel."),
        NOT_IN_SAME_VOICE(":warning: You have to be in the same voice channel as me."),
        PASSED("");

        private final String message;
        private final boolean passed;

        CheckResult(String message) {
            this.message = message;
            this.passed = message.equalsIgnoreCase("");
        }

        public String getMessage() {
            return message;
        }

        public boolean hasPassed() {
            return passed;
        }
    }

}
