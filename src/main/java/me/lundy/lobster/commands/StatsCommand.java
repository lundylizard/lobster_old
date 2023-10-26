package me.lundy.lobster.commands;

import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Optional;

public class StatsCommand extends BotCommand {

    @Override
    public void onCommand(CommandContext context) {

        String subcommand = context.getEvent().getSubcommandName();

        if (subcommand == null) {
            // Send personal stats
            return;
        }

        if (subcommand.equals("global")) {

            // Send global stats

        } else if (subcommand.equals("guild")) {

            // Send guild stats

        } else if (subcommand.equals("member")) {

            Optional<OptionMapping> memberOptional = Optional.ofNullable(context.getEvent().getOption("member"));

            if (memberOptional.isEmpty()) {
                context.getEvent().reply("Please use the member option and select a guild member for this command to work.").setEphemeral(true).queue();
                return;
            }

            Member member = memberOptional.get().getAsMember();

            // Send member stats

        } else {
            context.getEvent().reply("Unknown subcommand.").setEphemeral(true).queue();
        }

    }

    @Override
    public SlashCommandData getCommandData() {
        SubcommandData globalSubcommand = new SubcommandData("global", "View global statistics");
        SubcommandData guildSubcommand = new SubcommandData("guild", "View guild statistics");
        SubcommandData memberSubcommand = new SubcommandData("member", "View member statistics");
        OptionData memberOption = new OptionData(OptionType.USER, "member", "Member you want to check out", true);
        memberSubcommand.addOptions(memberOption);
        return Commands.slash("stats", "View global, guild or member statistics")
                .addSubcommands(globalSubcommand, guildSubcommand, memberSubcommand);
    }
}
