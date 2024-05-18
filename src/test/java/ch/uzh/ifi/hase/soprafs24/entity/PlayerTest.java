package ch.uzh.ifi.hase.soprafs24.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {
    private Player player;

    private final Word water = new Word("water");
    private final Word earth = new Word("earth");
    private final Word fire = new Word("fire");
    private final Word air = new Word("air");
    private final Word mud = new Word("mud");

    private List<Word> startingWords;

    @BeforeEach
    public void setup() {
        player = new Player();
        startingWords = new ArrayList<>();
        startingWords.add(water);
        startingWords.add(earth);
        startingWords.add(fire);
        startingWords.add(air);
    }

    @Test
    void addWords_success() {
        player.addWords(startingWords);
        assertEquals(startingWords, player.getWords());
        assertEquals(startingWords.size(), player.getPlayerWords().size());
    }
}
