package ch.uzh.ifi.hase.soprafs24.entity;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerWordTest {
    private PlayerWord playerWord;

    private Player player;

    private final Word word = new Word("titanium", 5, 0.05);

    @BeforeEach
    void setup() {
        player = new Player();
        player.setName("abcd");
        player.setId(1234);

        playerWord = new PlayerWord(player, word, 3);
    }

    @Test
    void equals_returnsTrue() {
        PlayerWord playerWord2 = new PlayerWord(player, word, 3);

        assertEquals(playerWord, playerWord2);
        assertEquals(playerWord2, playerWord);
    }

    @Test
    void notEqual_returnsFalse() {
        Word word2 = new Word("amber", 3, 0.27);
        PlayerWord playerWord2 = new PlayerWord(player, word2, 3);

        assertNotEquals(player, playerWord2);
        assertNotEquals(playerWord2, player);
    }

    @Test
    void compareWithNull_returnsFalse() {
        PlayerWord playerWord2 = null;

        assertNotEquals(playerWord, playerWord2);
    }
}
