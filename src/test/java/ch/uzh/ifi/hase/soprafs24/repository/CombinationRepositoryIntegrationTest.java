package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CombinationRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CombinationRepository combinationRepository;
    @Autowired
    private WordRepository wordRepository;

    @Test
    public void findByWord1AndWord2_success() {
        Word result = new Word("Steam");
        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");

        Combination combination = new Combination(word1, word2, result);

        entityManager.persist(combination);
        entityManager.flush();

        Combination found = combinationRepository.findByWord1AndWord2(combination.getWord1(), combination.getWord2());

        assertNotNull(found.getId());
        assertEquals(found.getResult(), combination.getResult());
        assertEquals(found.getWord1(), combination.getWord1());
        assertEquals(found.getWord2(), combination.getWord2());
    }

    @Test
    public void cascadingWords_success() {
        Word result = new Word("Steam");
        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");

        Combination combination = new Combination(word1, word2, result);

        entityManager.persist(combination);
        entityManager.flush();

        Word foundResult = wordRepository.findByName(result.getName());
        assertEquals(foundResult.getName(), result.getName());

        Word foundWord1 = wordRepository.findByName(word1.getName());
        assertEquals(foundWord1.getName(), word1.getName());

        Word foundWord2 = wordRepository.findByName(word2.getName());
        assertEquals(foundWord2.getName(), word2.getName());
    }
}
