package me.lundy.lobster.listeners.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class HelpButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if (event.getGuild() == null) return;
        String buttonId = event.getComponentId();

        if (buttonId.startsWith("help:")) {
            // String helpType = buttonId.substring(buttonId.lastIndexOf(":"));
            event.reply("ℹ️ The reason you are receiving this error is because you (or the bot) are not in a voice channel or you are not in the same voice channel. You can only use commands like these when you are in the same voice channel as lobster.").setEphemeral(true).queue();
            // TODO 3.0 -- implement different help buttons (not in vc is only implemented for now)
        }
    }
}
