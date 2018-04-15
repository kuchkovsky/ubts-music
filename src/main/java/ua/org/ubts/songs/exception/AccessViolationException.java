package ua.org.ubts.songs.exception;

public class AccessViolationException extends ServiceException {

    public AccessViolationException() {
    }

    public AccessViolationException(String message) {
        super(message);
    }

}
