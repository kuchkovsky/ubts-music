package ua.org.ubts.songs.exception;

public class SubscriptionNotActivatedException extends AccessViolationException {

    public SubscriptionNotActivatedException(String message) {
        super(message);
    }

}
