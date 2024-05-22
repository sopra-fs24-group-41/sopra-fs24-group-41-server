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
class CombinationRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CombinationRepository combinationRepository;

    @Test
    void findByWord1AndWord2_success() {
        Word word1 = new Word("Water");
        Word word2 = new Word("Fire");
        Word result = new Word("Steam");

        Combination combination = new Combination(word1, word2, result);
        entityManager.persist(word1);
        entityManager.persist(word2);
        entityManager.persist(result);
        entityManager.persist(combination);
        entityManager.flush();

        Combination found = combinationRepository.findByWord1AndWord2(combination.getWord1(), combination.getWord2());

        assertNotNull(found.getId());
        assertEquals(found.getResult(), combination.getResult());
        assertEquals(found.getWord1(), combination.getWord1());
        assertEquals(found.getWord2(), combination.getWord2());
    }
}
