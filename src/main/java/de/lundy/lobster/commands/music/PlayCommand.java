package de.lundy.lobster.commands.music;

import de.lundy.lobster.Lobsterbot;
import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.lavaplayer.spotify.SpotifyToYoutubeInterpreter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.io.IOException;
import java.util.Objects;

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
        var top = false;

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

        if (args[0].equalsIgnoreCase("top")) {
            top = true;
        }

        for (var i = top ? 1 : 0; i < args.length; i++) {
            link.append(args[i]).append(" ");
        }

        if (!isUrl(link.toString())) {
            link.insert(0, "ytsearch:");
        }

        if (!event.getMessage().getAttachments().isEmpty()) {
            if (link.length() != 0)
                link.delete(0, link.length()); // Clear link if there's anything idk why there would tho
            link.append(event.getMessage().getAttachments().get(0).getUrl());
        }

        if (link.isEmpty()) {
            channel.sendMessage(":warning: Please provide a file or an URL.").queue();
            return;
        }

        //Spotify doesn't allow direct playback from their API, so it's getting the data from the song and searches it on YouTube
        if (spotify.isSpotifyLink(link.toString())) {

            if (Lobsterbot.DEBUG) System.out.println(spotify.isSpotifyPlaylist(link.toString()));
            if (Lobsterbot.DEBUG)
                System.out.println(link.toString().replace("\\?si=.*$", "").replace("https://open.spotify.com/", ""));

            if (!spotify.isSpotifyPlaylist(link.toString())) {

                var spotifyId = spotify.getSpotifyIdFromLink(link.toString());
                link.delete(0, link.length());

                try {
                    link.append("ytsearch:").append(spotify.getArtistFromSpotify(spotifyId))
                            .append(" ")
                            .append(spotify.getSongNameFromSpotify(spotifyId));
                } catch (IOException | ParseException | SpotifyWebApiException e) {
                    e.printStackTrace();
                }

            } else {

                channel.sendMessage(":warning: Spotify Playlists may be a little buggy, gonna fix that sooner or later.").queue();
                var spotifyId = spotify.getSpotifyIdFromLink(link.toString());
                Playlist playlist = null;

                try {
                    playlist = spotify.getSpotifyPlaylist(spotifyId);
                } catch (IOException | ParseException | SpotifyWebApiException e) {
                    e.printStackTrace();
                }

                assert playlist != null;
                for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {

                    try {

                        if (playlistTrack.getTrack().getId() != null) {

                            PlayerManager.getInstance().loadAndPlay(event, ("ytsearch:" +
                                    spotify.getArtistFromSpotify(playlistTrack.getTrack().getId()) + " " +
                                    spotify.getSongNameFromSpotify(playlistTrack.getTrack().getId())).trim(), top, true);

                        }

                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        e.printStackTrace();
                    }

                }

                event.getChannel().sendMessage(":arrow_forward: Added " + playlist.getTracks().getTotal() + " songs to the queue.").queue();

            }

        }

        PlayerManager.getInstance().loadAndPlay(event, link.toString().trim(), top, false);

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
