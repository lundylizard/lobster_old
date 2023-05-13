package me.lundy.lobster.api.entities;

public class Playlist {

    private String id;
    private String name;
    private long ownerId;
    private String[] songs;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String[] getSongs() {
        return songs;
    }

}
