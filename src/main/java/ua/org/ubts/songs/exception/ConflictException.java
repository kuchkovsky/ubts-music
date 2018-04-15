package ua.org.ubts.songs.exception;

public class ConflictException extends ServiceException {

    public ConflictException() {
    }

    public ConflictException(String message) {
        super(message);
    }

}
