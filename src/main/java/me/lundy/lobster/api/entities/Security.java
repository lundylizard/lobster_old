package me.lundy.lobster.api.entities;

import java.time.LocalDateTime;

public class Security {

    private boolean banned;
    private String banReason;
    private LocalDateTime timestamp;

    public boolean isBanned() {
        return banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
