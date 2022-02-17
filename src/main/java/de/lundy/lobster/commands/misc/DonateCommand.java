package de.lundy.lobster.commands.misc;

import de.lundy.lobster.commands.impl.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DonateCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        event.getTextChannel().sendMessage(new EmbedBuilder()
                .setDescription("""
                        **DONATING**

                        Donating is not really necessary. I currently spend `9.79€` a month to keep the bot up. However, if you feel generous and want to support me and my work, you can do that here:
                        - [paypal.me](https://paypal.me/lukkyz1337)
                        - [Streamlabs Tips](https://streamlabs.com/iundylizard/tip)""")
                .setColor(Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getColor())
                .build()).queue();

    }

}
