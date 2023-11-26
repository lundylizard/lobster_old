package me.lundy.lobster.commands;

import core.GLA;
import genius.SongSearch;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

public class LyricsCommand extends BotCommand {

    private static final GLA GLA = new GLA();

    @Override
    public void onCommand(CommandContext context) {

        Optional<OptionMapping> searchOption = Optional.ofNullable(context.getEvent().getOption("search"));
        String searchString = searchOption.map(OptionMapping::getAsString).orElse("");
        context.getEvent().deferReply().queue();
        SongSearch.Hit song;

        try {
            song = this.searchSong(searchString);
        } catch (IOException e) {
            context.getEvent().replyFormat(Reply.ERROR_FETCHING_LYRICS.getMessage(), e.getMessage()).queue();
            return;
        }

        if (song == null) {
            context.getEvent().getHook().editOriginalFormat(Reply.COULD_NOT_FIND_LYRICS.getMessage(), searchString).queue();
            return;
        }

        String lyrics = song.fetchLyrics();
        String title = "## " + song.getArtist().getName() + " - " + song.getTitleWithFeatured() + "\n";

        if ((lyrics + title).length() > 4000) {
            context.getEvent().getHook().editOriginalFormat(Reply.LYRICS_TOO_LONG.getMessage(), song.getUrl()).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(context.getSelf().getColor())
                .setDescription(title)
                .setThumbnail(song.getThumbnailUrl());

        embedBuilder.appendDescription(lyrics);
        context.getEvent().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("lyrics", "Retrieve lyrics of a song")
                .addOptions(new OptionData(OptionType.STRING, "search", "Search for a specific song", true));
    }

    private SongSearch.Hit searchSong(String searchTerm) throws IOException {
        SongSearch songSearch = GLA.search(searchTerm);
        LinkedList<SongSearch.Hit> hits = songSearch.getHits();
        return hits.isEmpty() ? null : hits.get(0);
    }
}
