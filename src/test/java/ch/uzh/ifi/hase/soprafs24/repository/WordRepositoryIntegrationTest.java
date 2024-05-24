package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class WordRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WordRepository wordRepository;

    @BeforeEach
    void setup() {
        wordRepository.deleteAll();
    }

    @Test
    void findByName_success() {
        Word word = new Word("zaddy");

        entityManager.persist(word);
        entityManager.flush();

        Word found = wordRepository.findByName(word.getName());

        assertEquals(word, found);
    }

    @Test
    void findBySimilarName_success() {
        Word word = new Word("ice cream sandwiches");

        entityManager.persist(word);
        entityManager.flush();

        Word found = wordRepository.findBySimilarName(processString("Icecream sandwich"));

        assertEquals(processString(word.getName()), processString(found.getName()));
    }

    @Test
    void findBySimilarName_notFound() {
        Word word = new Word("ice scream sandwiches");

        entityManager.persist(word);
        entityManager.flush();

        Word found = wordRepository.findBySimilarName(processString("Icecream sandwich"));

        assertNull(found);
    }

    private static String processString(String input) {
        if (input == null) {
            return null;
        }

        String result = input.toLowerCase().replaceAll("\\s", "");

        if (result.endsWith("s")) {
            result = result.substring(0, result.length() - 1);
        } else if (result.endsWith("es")) {
            result = result.substring(0, result.length() - 2);
        }

        return result;
    }
}
