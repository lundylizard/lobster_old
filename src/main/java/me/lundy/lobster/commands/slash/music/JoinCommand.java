package me.lundy.lobster.commands.slash.music;

import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

@CommandInfo(name = "join", description = "Let lobster join your voice channel")
public class JoinCommand extends Command {

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            AudioChannel audioChannel = member.getVoiceState().getChannel();

            try {
                audioManager.openAudioConnection(audioChannel);
            } catch (InsufficientPermissionException e) {
                event.reply(":warning: I do not have enough permissions to join that channel.").setEphemeral(true).queue();
            } finally {
                event.reply("Joined voice channel `\uD83D\uDD0A " + audioChannel.getName() + "`").queue();
            }
        }
    }
}
