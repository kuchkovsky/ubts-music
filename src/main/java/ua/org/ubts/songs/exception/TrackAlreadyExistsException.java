package ua.org.ubts.songs.exception;

public class TrackAlreadyExistsException extends ConflictException {

    public TrackAlreadyExistsException() {
    }

    public TrackAlreadyExistsException(String message) {
        super(message);
    }

}
