package ch.uzh.ifi.hase.soprafs24.exceptions;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String name) {
        super(String.format("Word %s not found.", name));
    }
}
