package de.lundy.lobster.lavaplayer.spotify;

import de.lundy.lobster.Secrets;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;

import java.io.IOException;

public class SpotifyToYoutubeInterpreter {

    private final SpotifyApi spotifyApi;

    public SpotifyToYoutubeInterpreter() {

        this.spotifyApi = new SpotifyApi.Builder().setClientId(Secrets.SPOTIFY_CLIENT_ID).setClientSecret(Secrets.SPOTIFY_CLIENT_SECRET).build();

        var request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials creds;

        try {
            creds = request.grant_type("client_credentials").build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            return;
        }

        spotifyApi.setAccessToken(creds.getAccessToken());

    }

    //Returns an artist from a spotify track id
    public String getArtistFromSpotify(String spotifyId) throws IOException, ParseException, SpotifyWebApiException {

        var trackRequest = spotifyApi.getTrack(spotifyId).build();
        var track = trackRequest.execute();
        var artistsArray = track.getArtists();
        var artists = new StringBuilder();

        for (var artist : artistsArray) {
            artists.append(artist.getName()).append(" ");
        }

        return artists.toString().trim();

    }

    //Returns the song name from a spotify track id
    public String getSongNameFromSpotify(String spotifyId) throws IOException, ParseException, SpotifyWebApiException {
        var trackRequest = spotifyApi.getTrack(spotifyId).build();
        return trackRequest.execute().getName();
    }

    private @NotNull GetPlaylistRequest getPlaylistRequest(String spotifyId) {
        return spotifyApi.getPlaylist(spotifyId).build();
    }

    public Playlist getSpotifyPlaylist(String spotifyId) throws IOException, ParseException, SpotifyWebApiException {
        return getPlaylistRequest(spotifyId).execute();
    }

}
