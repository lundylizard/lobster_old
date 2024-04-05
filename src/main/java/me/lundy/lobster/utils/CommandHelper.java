package me.lundy.lobster.utils;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public record CommandHelper(SlashCommandInteractionEvent event) {

    public GuildVoiceState getExecutorVoiceState() {
        return event().getMember().getVoiceState();
    }

    public GuildVoiceState getSelfVoiceState() {
        return event().getGuild().getSelfMember().getVoiceState();
    }

    public VoiceChannel getExecutorVoiceChannel() {
        return getExecutorVoiceState().getChannel().asVoiceChannel();
    }

    public VoiceChannel getSelfVoiceChannel() {
        return getSelfVoiceState().getChannel().asVoiceChannel();
    }

    public boolean isSelfInVoiceChannel() {
        return getSelfVoiceState().inAudioChannel();
    }

    public boolean isExecutorInVoiceChannel() {
        return getExecutorVoiceState().inAudioChannel();
    }

    public boolean inSameVoice() {
        return isSelfInVoiceChannel() && isExecutorInVoiceChannel() && getSelfVoiceChannel().equals(getExecutorVoiceChannel());
    }

}
