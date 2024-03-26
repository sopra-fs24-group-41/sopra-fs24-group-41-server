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

    @Test
    public void findByWord1AndWord2_success() {
        Word result = new Word();
        result.setName("Steam");

        Word word1 = new Word();
        word1.setName("Water");

        Word word2 = new Word();
        word2.setName("Fire");

        Combination combination = new Combination();
        combination.setResult(result);
        combination.setWord1(word1);
        combination.setWord2(word2);

        entityManager.persist(result);
        entityManager.persist(word1);
        entityManager.persist(word2);
        entityManager.persist(combination);
        entityManager.flush();

        Combination found = combinationRepository.findByWord1AndWord2(combination.getWord1(), combination.getWord2());

        assertNotNull(found.getId());
        assertEquals(found.getResult(), combination.getResult());
        assertEquals(found.getWord1(), combination.getWord1());
        assertEquals(found.getWord2(), combination.getWord2());
    }
}
