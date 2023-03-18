package me.lundy.lobster.commands.impl.music;

import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.commands.IgnoreChecks;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

@IgnoreChecks
public class JoinCommand extends BotCommand {

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

    @Override
    public String name() {
        return "join";
    }

    @Override
    public String description() {
        return "Join your voice channel";
    }
}
