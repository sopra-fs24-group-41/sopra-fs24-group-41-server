package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    private final Word water = new Word("water");
    private final Word earth = new Word("earth");
    private final Word fire = new Word("fire");
    private final Word air = new Word("air");
    private final Word mud = new Word("mud");

    private List<Word> startingWords;

    @BeforeEach
    void setup() {
        player = new Player();
        startingWords = new ArrayList<>();
        startingWords.add(water);
        startingWords.add(earth);
        startingWords.add(fire);
        startingWords.add(air);
    }

    @Test
    void equals_returnsTrue() {
        player.setId(1234);
        player.setToken("abcd");

        Player player2 = new Player();
        player2.setId(1234);
        player2.setToken("abcd");

        assertTrue(player.equals(player2));
        assertTrue(player2.equals(player));
    }

    @Test
    void notEqual_returnsFalse() {
        player.setId(12345);
        player.setToken("abcd");

        Player player2 = new Player();
        player2.setId(1234);
        player2.setToken("abcd");

        assertFalse(player.equals(player2));
        assertFalse(player2.equals(player));
    }

    @Test
    void compareWithNull_returnsFalse() {
        player.setId(12345);
        player.setToken("abcd");

        Player player2 = null;

        assertFalse(player.equals(player2));
    }

    @Test
    void addWords_success() {
        player.addWords(startingWords);
        assertEquals(startingWords, player.getWords());
        assertEquals(startingWords.size(), player.getPlayerWords().size());
    }

    @Test
    void addWordsWithUses_success() {
        player.addWords(startingWords, 5);

        for (Word word : startingWords) {
            assertEquals(word, player.getPlayerWord(word).getWord());
            assertEquals(5, player.getPlayerWord(word).getUses());
        }
    }

    @Test
    void addWordWithUses_newPlayerWord_success() {
        player.addWord(water, 5);

        assertEquals(water, player.getPlayerWord(water).getWord());
        assertEquals(5, player.getPlayerWord(water).getUses());
    }

    @Test
    void addWordWithUses_existingPlayerWord_success() {
        player.addWord(water, 5);
        player.addWord(water, 3);

        assertEquals(water, player.getPlayerWord(water).getWord());
        assertEquals(8, player.getPlayerWord(water).getUses());
    }

    @Test
    void getPlayerWord_success() {
        player.addWords(startingWords);

        PlayerWord result = player.getPlayerWord(water);
        assertEquals(water, result.getWord());
    }
}
