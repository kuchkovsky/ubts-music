package ua.org.ubts.songs.exception;

public class OrderNotFoundException extends DatabaseItemNotFoundException {

    public OrderNotFoundException() {
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

}
