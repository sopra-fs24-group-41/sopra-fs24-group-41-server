package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.PlayerWord;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlayerWordRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerWordRepository playerWordRepository;

    @Test
    void findAllByPlayer() {
        // given
        Player player = new Player("123", "test", null);

        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");
        Word word3 = new Word("Steam");

        PlayerWord playerWord1 = new PlayerWord(player, word1);
        PlayerWord playerWord2 = new PlayerWord(player, word2);
        PlayerWord playerWord3 = new PlayerWord(player, word3);

        entityManager.persist(playerWord1);
        entityManager.persist(playerWord2);
        entityManager.persist(playerWord3);
        entityManager.flush();

        PlayerWord[] playerWords = new PlayerWord[]{playerWord1, playerWord2, playerWord3};

        // when
        List<PlayerWord> found = playerWordRepository.findAllByPlayer(player);

        // then
        assertArrayEquals(playerWords, found.toArray());
    }

    @Test
    void findAllByWord() {
        // given
        Player player = new Player("123", "test", null);

        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");
        Word word3 = new Word("Steam");

        PlayerWord playerWord1 = new PlayerWord(player, word1);
        PlayerWord playerWord2 = new PlayerWord(player, word2);
        PlayerWord playerWord3 = new PlayerWord(player, word3);

        entityManager.persist(playerWord1);
        entityManager.persist(playerWord2);
        entityManager.persist(playerWord3);
        entityManager.flush();

        PlayerWord[] playerWords = new PlayerWord[]{playerWord1};

        // when
        List<PlayerWord> found = playerWordRepository.findAllByWord(word1);

        // then
        assertArrayEquals(playerWords, found.toArray());
    }
}
