package ua.org.ubts.songs.exception;

public class TrackNotPuchasedException extends AccessViolationException {

    public TrackNotPuchasedException() {
    }

    public TrackNotPuchasedException(String message) {
        super(message);
    }

}
