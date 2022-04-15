package de.lundy.lobster.commands.music;

import de.lundy.lobster.commands.impl.Command;
import de.lundy.lobster.lavaplayer.PlayerManager;
import de.lundy.lobster.lavaplayer.spotify.SpotifyToYoutubeInterpreter;
import de.lundy.lobster.lavaplayer.spotify.SpotifyUtils;
import de.lundy.lobster.utils.BotUtils;
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

        if (! (memberVoiceState != null && memberVoiceState.inVoiceChannel())) {
            channel.sendMessage(":warning: You are not in a voice channel.").queue();
            return;
        }

        if (! (selfVoiceState != null && selfVoiceState.inVoiceChannel())) {
            var audioManager = event.getGuild().getAudioManager();
            var memberChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
            audioManager.openAudioConnection(memberChannel);
            assert memberChannel != null;
            event.getChannel().sendMessage(":loud_sound: Connecting to voice channel `\uD83D\uDD0A " + memberChannel.getName() + "`").queue();
        }

        var top = false;
        if (args.length > 0) {
            top = args[0].equalsIgnoreCase("top");
        }

        if (!event.getMessage().getAttachments().isEmpty()) {
            PlayerManager.getInstance().loadAndPlay(event, event.getMessage().getAttachments().get(0).getUrl(), top, false);
            return;
        }

        for (var i = top ? 1 : 0; i < args.length; i++) {
            link.append(args[i]).append(" ");
        }

        if (! BotUtils.isUrl(link.toString())) {
            link.insert(0, "ytsearch:");
        }

        if (link.isEmpty()) {
            channel.sendMessage(":warning: Please provide a file or an URL.").queue();
            return;
        }

        //Spotify doesn't allow direct playback from their API, so it's getting the data from the song and searches it on YouTube
        if (SpotifyUtils.isSpotifyLink(link.toString())) {

            var spotify = new SpotifyToYoutubeInterpreter();
            var spotifyId = SpotifyUtils.getSpotifyIdFromLink(link.toString());

            if (! SpotifyUtils.isSpotifyPlaylist(link.toString())) {

                link.delete(0, link.length()); // Clear link

                try {

                    link.append("ytsearch:").append(spotify.getArtistFromSpotify(spotifyId)).append(" ").append(spotify.getSongNameFromSpotify(spotifyId));

                } catch (IOException | ParseException | SpotifyWebApiException e) {
                    e.printStackTrace();
                }

            } else {

                Playlist playlist;

                try {
                    playlist = spotify.getSpotifyPlaylist(spotifyId);
                } catch (IOException | ParseException | SpotifyWebApiException e) {
                    event.getTextChannel().sendMessage(":warning: Could not get Spotify playlist: " + e.getMessage()).queue();
                    return;
                }

                for (PlaylistTrack playlistTrack : playlist.getTracks().getItems()) {

                    try {

                        if (playlistTrack.getTrack().getId() != null) {

                            PlayerManager.getInstance().loadAndPlay(event, ("ytsearch:" +
                                    spotify.getArtistFromSpotify(playlistTrack.getTrack().getId()) + " " +
                                    spotify.getSongNameFromSpotify(playlistTrack.getTrack().getId())).trim(), top, true);

                        }

                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        event.getTextChannel().sendMessage(":warning: Could not get Spotify song `" + playlistTrack.getTrack().getName() + "`").queue();
                        return;
                    }

                }

                event.getChannel().sendMessage("Added " + playlist.getTracks().getTotal() + " songs to the queue.").queue();

            }
        }

        PlayerManager.getInstance().loadAndPlay(event, link.toString().trim(), top, false);

    }
}