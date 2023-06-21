package me.lundy.lobster.lavaplayer;

public class AudioTrackUserData {

    private final String searchTerm;
    private final String userMention;

    public AudioTrackUserData(String searchTerm, String userMention) {
        this.searchTerm = searchTerm;
        this.userMention = userMention;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public String getUserMention() {
        return userMention;
    }
}
