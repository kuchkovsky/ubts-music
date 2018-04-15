package ua.org.ubts.songs.exception;

public class FileNotFoundException extends FileOperationException {

    public FileNotFoundException() {
    }

    public FileNotFoundException(String message) {
        super(message);
    }

}
