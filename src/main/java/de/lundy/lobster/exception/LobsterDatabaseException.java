package de.lundy.lobster.exception;

public class LobsterDatabaseException extends RuntimeException {

    public LobsterDatabaseException(String message) {
        super(message);
    }

    public LobsterDatabaseException(String message, Object... args) {
        super(String.format(message, args));
    }

}
