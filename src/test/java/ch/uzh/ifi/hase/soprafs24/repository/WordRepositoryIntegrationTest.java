package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class WordRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WordRepository wordRepository;

    @Test
    public void findByName_success() {
        Word word = new Word("Zaddy");

        entityManager.persist(word);
        entityManager.flush();

        Word found = wordRepository.findByName(word.getName());

        assertEquals(found.getName(), word.getName());
    }
}
