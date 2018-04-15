package ua.org.ubts.songs.exception;

public class UserNotFoundException extends DatabaseItemNotFoundException {

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}
