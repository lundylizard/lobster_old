package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import core.GLA;
import genius.SongSearch;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.lavaplayer.AudioTrackUserData;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import me.lundy.lobster.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.SplitUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@CommandInfo(name = "lyrics", description = "Retrieve lyrics of a song")
public class LyricsCommand extends Command implements CommandOptions {

    private final GLA gla = new GLA();

    @Override
    public void onCommand(CommandContext context) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack playingTrack = musicManager.audioPlayer.getPlayingTrack();
        Optional<OptionMapping> searchOption = Optional.ofNullable(context.getEvent().getOption("search"));

        String searchString = "";

        if (searchOption.isEmpty()) {

            if (playingTrack == null) {
                context.getEvent().reply(":warning: There is currently no track playing").setEphemeral(true).queue();
                return;
            }

            if (!context.selfInVoice()) {
                context.getEvent().reply(":warning: I am not in a voice channel").setEphemeral(true).queue();
                return;
            }

            if (!context.inSameVoice()) {
                context.getEvent().reply(":warning: We are not in the same voice channel").setEphemeral(true).queue();
                return;
            }

            searchString = playingTrack.getUserData(AudioTrackUserData.class).getSearchTerm();
        }

        if (searchOption.isPresent()) {
            searchString = searchOption.get().getAsString();
        }

        System.out.println(searchString + " = " + StringUtils.sanitizeTrackTitle(searchString));
        context.getEvent().deferReply().queue();

        try {

            SongSearch search = gla.search(searchString);
            LinkedList<SongSearch.Hit> hits = search.getHits();

            if (hits.isEmpty()) {
                context.getEvent().getHook().editOriginal(":warning: Could not find lyrics for song, please try to search manually.").queue();
                return;
            }

            SongSearch.Hit song = hits.get(0);
            String lyrics = song.fetchLyrics();

            lyrics = "## " + song.getArtist().getName() + " - " + song.getTitleWithFeatured() + "\n" + lyrics;

            if (lyrics.length() > 6000) {
                context.getEvent().getHook().editOriginal("Lyrics is too long to be displayed: " + song.getUrl()).queue();
                return;
            }

            List<String> embedText = SplitUtil.split(lyrics, 2000, StringUtils.onTwoNewLinesStrategy(), SplitUtil.Strategy.ANYWHERE);
            List<MessageEmbed> embeds = new ArrayList<>();

            for (String lyricsPart : embedText) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(context.getSelf().getColor())
                        .setDescription(lyricsPart)
                        .setThumbnail(song.getThumbnailUrl());
                embeds.add(embedBuilder.build());
            }

            context.getEvent().getHook().editOriginalEmbeds(embeds).queue();

        } catch (IOException e) {
            context.getEvent().reply(":warning: An error occurred while fetching the lyrics.").queue();
            getLogger().error("An error occurred while fetching the lyrics", e);
        }
    }

    @Override
    public List<OptionData> options() {
        OptionData searchOption = new OptionData(OptionType.STRING, "search", "Search for a specific song", false);
        return List.of(searchOption);
    }
}
