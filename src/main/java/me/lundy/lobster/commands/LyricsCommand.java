package me.lundy.lobster.commands;

import core.GLA;
import genius.SongSearch;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.utils.Reply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
    public void execute(SlashCommandInteractionEvent event) {

        var searchOption = Optional.ofNullable(event.getOption("search"));
        String searchString = searchOption.map(OptionMapping::getAsString).orElse("");
        event.deferReply().queue();
        SongSearch.Hit song;

        try {
            song = this.searchSong(searchString);
        } catch (IOException e) {
            event.replyFormat(Reply.ERROR_FETCHING_LYRICS.getMessage(), e.getMessage()).queue();
            return;
        }

        if (song == null) {
            event.getHook().editOriginalFormat(Reply.COULD_NOT_FIND_LYRICS.getMessage(), searchString).queue();
            return;
        }

        String lyrics = song.fetchLyrics();
        String artistName = song.getArtist().getName();
        String title = song.getTitleWithFeatured();
        String embedTitle = String.format("## %s - %s\n", artistName, title);

        if ((lyrics + embedTitle).length() > 4000) {
            event.getHook().editOriginalFormat(Reply.LYRICS_TOO_LONG.getMessage(), song.getUrl()).queue();
            return;
        }

        var embedBuilder = new EmbedBuilder()
                .setColor(event.getGuild().getSelfMember().getColor())
                .setDescription(embedTitle)
                .setThumbnail(song.getThumbnailUrl());

        embedBuilder.appendDescription(lyrics);
        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData searchOption = new OptionData(OptionType.STRING, "search", "Search for a specific song", true);
        return Commands.slash("lyrics", "Retrieve lyrics of a song").addOptions(searchOption);
    }

    private SongSearch.Hit searchSong(String searchTerm) throws IOException {
        SongSearch songSearch = GLA.search(searchTerm);
        LinkedList<SongSearch.Hit> hits = songSearch.getHits();
        return hits.isEmpty() ? null : hits.getFirst();
    }
}
