package me.lundy.lobster.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import core.GLA;
import genius.SongSearch;
import me.lundy.lobster.commands.BotCommand;
import me.lundy.lobster.commands.IgnoreChecks;
import me.lundy.lobster.lavaplayer.GuildMusicManager;
import me.lundy.lobster.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@IgnoreChecks
public class LyricsCommand extends BotCommand {

    private final GLA gla = new GLA();

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        Optional<OptionMapping> searchOption = Optional.ofNullable(event.getOption("search"));
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioTrack playingTrack = musicManager.audioPlayer.getPlayingTrack();
        String searchString = "";

        if (searchOption.isEmpty()) {
            if (playingTrack == null) {
                event.reply(":warning: There is currently no track playing.").queue();
                return;
            } else {
                searchString = playingTrack.getInfo().title;
            }
        }

        if (searchOption.isPresent()) {
            searchString = searchOption.get().getAsString();
        }

        try {

            SongSearch search = gla.search(searchString);
            LinkedList<SongSearch.Hit> hits = search.getHits();

            if (hits.isEmpty()) {
                event.reply(":warning: Could not find lyrics for song, please try to search manually.").queue();
                return;
            }

            SongSearch.Hit song = hits.get(0);
            String lyrics = song.fetchLyrics();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(event.getGuild().getSelfMember().getColor())
                    .setDescription(lyrics)
                    .setTitle(String.format("%s %s Lyrics", song.getArtist().getName(), song.getTitle()), song.getUrl())
                    .setFooter("Powered by genius.com");

            event.replyEmbeds(embedBuilder.build()).queue();

        } catch (IOException e) {
            event.reply(":warning: An error occurred while fetching the lyrics.").queue();
            getLogger().error("An error occurred while fetching the lyrics", e);
        }
    }

    @Override
    public String name() {
        return "lyrics";
    }

    @Override
    public String description() {
        return "Retrieve lyrics of a song";
    }

    @Override
    public List<OptionData> options() {
        OptionData searchOption = new OptionData(OptionType.STRING, "search", "Search for a specific song", false);
        return List.of(searchOption);
    }
}
