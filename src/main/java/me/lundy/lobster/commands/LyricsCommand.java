package me.lundy.lobster.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import core.GLA;
import genius.SongSearch;
import me.lundy.lobster.command.Command;
import me.lundy.lobster.command.CommandContext;
import me.lundy.lobster.command.CommandInfo;
import me.lundy.lobster.command.CommandOptions;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@CommandInfo(name = "lyrics", description = "Retrieve lyrics of a song")
public class LyricsCommand extends Command implements CommandOptions {

    private final GLA gla = new GLA();

    @Override
    public void onCommand(CommandContext context) {

        if (!context.executorInVoice()) {
            context.getEvent().reply(":warning: You are not in a voice channel").setEphemeral(true).queue();
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

        Optional<OptionMapping> searchOption = Optional.ofNullable(context.getEvent().getOption("search"));
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(context.getGuild());
        AudioTrack playingTrack = musicManager.audioPlayer.getPlayingTrack();

        String searchString = "";

        if (searchOption.isEmpty()) {
            searchString = playingTrack.getInfo().title;
        }

        if (searchOption.isPresent()) {
            searchString = searchOption.get().getAsString();
        }

        try {

            SongSearch search = gla.search(searchString);
            LinkedList<SongSearch.Hit> hits = search.getHits();

            if (hits.isEmpty()) {
                context.getEvent().reply(":warning: Could not find lyrics for song, please try to search manually.").setEphemeral(true).queue();
                return;
            }

            SongSearch.Hit song = hits.get(0);
            String lyrics = song.fetchLyrics();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(context.getSelf().getColor())
                    .setDescription(lyrics)
                    .setTitle(String.format("%s %s Lyrics", song.getArtist().getName(), song.getTitle()), song.getUrl())
                    .setFooter("Powered by genius.com");

            context.getEvent().replyEmbeds(embedBuilder.build()).queue();

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
