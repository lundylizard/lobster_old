package de.lundy.lobster.lavaplayer.spotify;

import de.lundy.lobster.Secrets;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;

import java.io.IOException;

public class SpotifyToYoutubeInterpreter {

    private SpotifyApi spotifyApi;

    public SpotifyToYoutubeInterpreter() {
        initSpotify();
    }

    //Initializes the spotify api
    private void initSpotify() {

        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(Secrets.SPOTIFY_CLIENT_ID)
                .setClientSecret(Secrets.SPOTIFY_CLIENT_SECRET)
                .build();

        var request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials creds = null;

        try {
            creds = request.grant_type("client_credentials").build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }

        assert creds != null;
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

        return artists.toString();

    }

    //Returns the song name from a spotify track id
    public String getSongNameFromSpotify(String spotifyId) throws IOException, ParseException, SpotifyWebApiException {
        var trackRequest = spotifyApi.getTrack(spotifyId).build();
        return trackRequest.execute().getName();
    }

    @Contract("_ -> new")
    private @NotNull GetPlaylistRequest getPlaylistRequest(String spotifyId) {
        return spotifyApi.getPlaylist(spotifyId).build();
    }

    public Playlist getSpotifyPlaylist(String spotifyId) throws IOException, ParseException, SpotifyWebApiException {

        return getPlaylistRequest(spotifyId).execute();

    }

    public boolean isSpotifyLink(@NotNull String link) {
        return link.toLowerCase().startsWith("https://open.spotify.com/");
    }

    public boolean isSpotifyPlaylist(@NotNull String link) {
        return link.replace("\\?si=.*$", "").replace("https://open.spotify.com/", "").startsWith("playlist/");
    }

    public String getSpotifyIdFromLink(@NotNull String link) {
        return link.replaceAll("\\?si=.*$", "").replace("https://open.spotify.com/" + (isSpotifyPlaylist(link) ? "playlist/" : "track/"), "");
    }

}
