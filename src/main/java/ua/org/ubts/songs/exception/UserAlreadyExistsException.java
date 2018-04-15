package ua.org.ubts.songs.exception;

public class UserAlreadyExistsException extends ConflictException {

    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
