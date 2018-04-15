package ua.org.ubts.songs.exception;

public class DatabaseItemNotFoundException extends ServiceException {

    public DatabaseItemNotFoundException() {
    }

    public DatabaseItemNotFoundException(String message) {
        super(message);
    }

}
