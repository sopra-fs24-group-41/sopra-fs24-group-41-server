package ch.uzh.ifi.hase.soprafs24.exceptions;

public class CombinationNotFoundException extends RuntimeException {
    public CombinationNotFoundException(String name1, String name2) {
        super(String.format("Combination %s + %s not found.", name1, name2));
    }
}
