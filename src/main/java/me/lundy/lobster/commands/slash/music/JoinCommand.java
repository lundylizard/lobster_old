package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.checks.CommandCheck;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.checks.RunCheck;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

@CommandInfo(name = "join", description = "Let lobster join your voice channel")
public class JoinCommand extends Command {

    @Override
    @RunCheck(check = CommandCheck.EXECUTOR_IN_VOICE)
    public void onCommand(SlashCommandInteractionEvent event) {

        Guild guild = event.getGuild();
        if (guild == null) return;
        Member member = event.getMember();
        if (member == null) return;
        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
        if (selfVoiceState == null) return;
        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null) return;

        if (!selfVoiceState.inAudioChannel()) {

            AudioManager audioManager = event.getGuild().getAudioManager();
            AudioChannel audioChannel = memberVoiceState.getChannel();

            try {
                audioManager.openAudioConnection(audioChannel);
            } catch (InsufficientPermissionException e) {
                event.reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
            } finally {
                event.replyFormat("Joined voice channel %s", audioChannel != null ? "`\uD83D\uDD0A " + audioChannel.getName() + "`" : "").queue();
            }
        } else {
            event.reply("I am already in a voice channel.").queue();
        }
    }
}
