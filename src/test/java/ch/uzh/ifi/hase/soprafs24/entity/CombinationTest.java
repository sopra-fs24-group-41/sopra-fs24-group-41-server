package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CombinationTest {
    private Combination combination;

    private final Word word1 = new Word("water");
    private final Word word2 = new Word("fire");
    private final Word result = new Word("steam");

    private final Word result2 = new Word("river");

    @BeforeEach
    void setup() {
        combination = new Combination(word1, word2, result);
    }

    @Test
    void equals_returnsTrue() {
        assertTrue(combination.equals(combination));
    }

    @Test
    void notEqual_returnsFalse() {
        Combination combination2 = new Combination(word1, word1, result2);

        assertFalse(combination.equals(combination2));
        assertFalse(combination2.equals(combination));
    }

    @Test
    void compareWithNull_returnsFalse() {
        Combination combination2 = null;

        assertFalse(combination.equals(combination2));
    }
}
