package me.lundy.lobster.command.checks;

public enum CommandCheck {

    SELF_IN_VOICE("I am not in a voice channel."),
    EXECUTOR_IN_VOICE("You are not in a voice channel."),
    IN_SAME_VOICE("You have to be in the same voice channel as me.");

    private final String failMessage;

    CommandCheck(String failMessage) {
        this.failMessage = failMessage;
    }

    public String getFailMessage() {
        return failMessage;
    }

}
