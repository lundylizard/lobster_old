package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.lavaplayer.spotify.SpotifyToYoutubeInterpreter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class PlayCommand implements Command {

    @Override
    public void action(String[] args, @NotNull MessageReceivedEvent event) {

        var link = new StringBuilder();
        var channel = event.getTextChannel();
        var self = Objects.requireNonNull(event.getMember()).getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();
        var spotify = new SpotifyToYoutubeInterpreter();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {

            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;

        }

        assert selfVoiceState != null;
        if (!selfVoiceState.inVoiceChannel()) {

            var audioManager = event.getGuild().getAudioManager();
            var memberChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
            audioManager.openAudioConnection(memberChannel);
            assert memberChannel != null;
            event.getChannel().sendMessage(":loud_sound: Connecting to voice channel `\uD83D\uDD0A " + memberChannel.getName() + "`").queue();

        }

            for (var arg : args) {
                link.append(arg).append(" ");
            }

            link.delete(link.length(), link.length() - 1); // Remove the last whitespace

            if (!isUrl(link.toString())) {
                link.insert(0, "ytsearch:");
            }

        if (!event.getMessage().getAttachments().isEmpty()) {

            link.delete(0, link.length());
            link.append(event.getMessage().getAttachments().get(0).getUrl());

        }

        if (link.isEmpty()) {

            channel.sendMessage(":warning: Please provide a file or an URL.").queue();
            return;

        }

        //Spotify doesn't allow direct playback from their api, so it's getting the data from the song and searches it on youtube
        if (spotify.isSpotifyLink(link.toString())) {

            var spotifyId = spotify.getSpotifyIdFromLink(link.toString());
            link.delete(0, link.length());

            try {
                link.append("ytsearch:").append(spotify.getArtistFromSpotify(spotifyId)).append(" ").append(spotify.getSongNameFromSpotify(spotifyId));
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                e.printStackTrace();
            }

            if (new Random().nextInt(100) < 20) {
                event.getTextChannel().sendMessage(":information_source: Due to spotifys API limitations the bot is looking up the song on YouTube. If it can't find the song requested, please search for it or put a link from YouTube.").queue();
            }

        }

        PlayerManager.getInstance().loadAndPlay(event, link.toString());

    }

    private boolean isUrl(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
