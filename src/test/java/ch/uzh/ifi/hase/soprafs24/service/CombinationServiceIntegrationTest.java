package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class CombinationServiceIntegrationTest {

    @Qualifier("combinationRepository")
    @Autowired
    private CombinationRepository combinationRepository;

    @Qualifier("wordRepository")
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private CombinationService combinationService;

    @BeforeEach
    public void setup() {
        combinationRepository.deleteAll();
        wordRepository.deleteAll();
    }

    @Test
    public void getCombination_manyCombinations_success() {
        Combination combo1 = combinationService.getCombination(new Word("fire"), new Word("water"));
        Combination combo2 = combinationService.getCombination(new Word("fire"), new Word("water"));
        Combination combo3 = combinationService.getCombination(new Word("fire"), new Word("earth"));

        assertEquals(combo1.getWord1().getName(), combo2.getWord1().getName());
        assertEquals(combo1.getWord2().getName(), combo2.getWord2().getName());
        assertEquals(combo1.getResult().getName(), combo2.getResult().getName());

        assertEquals(combo1.getWord1().getName(), combo3.getWord1().getName());
        assertNotEquals(combo1.getWord2().getName(), combo3.getWord2().getName());
    }
}