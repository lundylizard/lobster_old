package de.lundy.lobster.lavaplayer.spotify;

import de.lundy.lobster.Lobsterbot;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

public class SpotifyToYoutubeInterpreter {

    private SpotifyApi spotifyApi;

    public SpotifyToYoutubeInterpreter() {

        try {
            initSpotify();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }

    //Initializes the spotify api
    private void initSpotify() throws ParseException, SpotifyWebApiException, IOException {

        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(Lobsterbot.SPOTIFY_CLIENT_ID)
                .setClientSecret(Lobsterbot.SPOTIFY_CLIENT_SECRET)
                .build();
        var request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        var creds = request.grant_type("client_credentials").build().execute();
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

    public boolean isSpotifyLink(@NotNull String link) {
        return link.toLowerCase().startsWith("https://open.spotify.com/");
    }

    public boolean isSpotifyPlaylist(@NotNull String link) {
        return link.replace("\\?si=.*$", "").replace("https://open.spotify.com/", "").equalsIgnoreCase("playlist/");
    }

    public String getSpotifyIdFromLink(@NotNull String link) {
        return link.replaceAll("\\?si=.*$", "").replace("https://open.spotify.com/" + (isSpotifyPlaylist(link) ? "playlist/" : "track/"), "");
    }

}
