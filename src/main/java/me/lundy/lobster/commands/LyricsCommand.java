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

    private final GLA gla = new GLA();

    @Override
    public void onCommand(CommandContext context) {

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack playingTrack = musicManager.audioPlayer.getPlayingTrack();

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

        Optional<OptionMapping> searchOption = Optional.ofNullable(context.getEvent().getOption("search"));
        String searchString = searchOption.map(OptionMapping::getAsString).orElseGet(() -> playingTrack.getUserData(AudioTrackUserData.class).getSearchTerm());
        context.getEvent().deferReply().queue();

        try {

            SongSearch.Hit song = searchSong(searchString);

            if (song == null) {
                context.getEvent().getHook().editOriginalFormat(Reply.COULD_NOT_FIND_LYRICS.getMessage(), searchString).queue();
                return;
            }

            String lyrics = song.fetchLyrics();
            String title = "## " + song.getArtist().getName() + " - " + song.getTitleWithFeatured() + "\n";

            if ((lyrics + title).length() > 6000) {
                context.getEvent().getHook().editOriginalFormat(Reply.LYRICS_TOO_LONG.getMessage(), song.getUrl()).queue();
                return;
            }

            List<String> embedText = SplitUtil.split(lyrics, 4000, SplitUtil.Strategy.NEWLINE);
            List<MessageEmbed> embeds = new ArrayList<>();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(context.getSelf().getColor());
            embedBuilder.appendDescription(title);
            embedBuilder.setThumbnail(song.getThumbnailUrl());

            for (String s : embedText) {
                embedBuilder.appendDescription(s);
                embeds.add(embedBuilder.build());
            }

            context.getEvent().getHook().editOriginalEmbeds(embeds).queue();

        } catch (IOException e) {
            context.getEvent().reply(Reply.ERROR_FETCHING_LYRICS.getMessage()).queue();
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        OptionData optionSearch = new OptionData(OptionType.STRING, "search", "Search for a specific song", false);
        return Commands.slash("lyrics", "Retrieve lyrics of a song").addOptions(optionSearch);
    }

    private SongSearch.Hit searchSong(String searchTerm) throws IOException {
        SongSearch songSearch = this.gla.search(searchTerm);
        LinkedList<SongSearch.Hit> hits = songSearch.getHits();
        if (hits.isEmpty()) return null;
        return hits.get(0);
    }

}
