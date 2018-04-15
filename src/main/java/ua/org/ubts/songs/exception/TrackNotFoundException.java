package ua.org.ubts.songs.exception;

public class TrackNotFoundException extends DatabaseItemNotFoundException {

    public TrackNotFoundException() {
    }

    public TrackNotFoundException(String message) {
        super(message);
    }

}
