package de.joshicodes.rja.exception;

public class RJAPingException extends RuntimeException {

    public RJAPingException(String message) {
        super(message);
    }

    public RJAPingException(String message, Throwable cause) {
        super(message, cause);
    }

}
