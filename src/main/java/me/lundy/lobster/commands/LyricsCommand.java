package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import core.GLA;
import genius.SongSearch;
import me.lundy.lobster.command.BotCommand;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.lavaplayer.AudioTrackUserData;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.Reply;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.SplitUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class LyricsCommand extends BotCommand {

    private static final GLA gla = new GLA();

    @Override
    public void onCommand(CommandContext context) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack playingTrack = musicManager.audioPlayer.getPlayingTrack();
        Optional<OptionMapping> searchOption = Optional.ofNullable(context.getEvent().getOption("search"));

        String searchString = "";

        if (searchOption.isEmpty()) {

            if (playingTrack == null) {
                context.getEvent().reply(Reply.NO_TRACK_PLAYING.getMessage()).setEphemeral(true).queue();
                return;
            }

            if (!context.selfInVoice()) {
                context.getEvent().reply(Reply.SELF_NOT_IN_VOICE.getMessage()).setEphemeral(true).queue();
                return;
            }

            if (!context.inSameVoice()) {
                context.getEvent().reply(Reply.NOT_IN_SAME_VOICE.getMessage()).setEphemeral(true).queue();
                return;
            }

            searchString = playingTrack.getUserData(AudioTrackUserData.class).getSearchTerm();
        }

        if (searchOption.isPresent()) {
            searchString = searchOption.get().getAsString();
        }

        context.getEvent().deferReply().queue();

        try {

            SongSearch search = gla.search(searchString);
            LinkedList<SongSearch.Hit> hits = search.getHits();

            if (hits.isEmpty()) {
                context.getEvent().getHook().editOriginalFormat(Reply.COULD_NOT_FIND_LYRICS.getMessage(), searchString).queue();
                return;
            }

            SongSearch.Hit song = hits.get(0);
            String lyrics = song.fetchLyrics();

            lyrics = "## " + song.getArtist().getName() + " - " + song.getTitleWithFeatured() + "\n" + lyrics;

            if (lyrics.length() > 6000) {
                context.getEvent().getHook().editOriginalFormat(Reply.LYRICS_TOO_LONG.getMessage(), song.getUrl()).queue();
                return;
            }

            List<String> embedText = SplitUtil.split(lyrics, 2000, StringUtils.onTwoNewLinesStrategy(), SplitUtil.Strategy.ANYWHERE);
            List<MessageEmbed> embeds = new ArrayList<>();

            for (String lyricsPart : embedText) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(context.getSelf().getColor());
                embedBuilder.setDescription(lyricsPart);
                embedBuilder.setThumbnail(song.getThumbnailUrl());
                embeds.add(embedBuilder.build());
            }

            context.getEvent().getHook().editOriginalEmbeds(embeds).queue();

        } catch (IOException e) {
            context.getEvent().reply(Reply.ERROR_FETCHING_LYRICS.getMessage()).queue();
            // getLogger().error("An error occurred while fetching the lyrics", e);
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSearch = new OptionData(OptionType.STRING, "search", "Search for a specific song", false);
        return Commands.slash("lyrics", "Retrieve lyrics of a song").addOptions(optionSearch);
    }

}
