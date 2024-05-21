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

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
class CombinationServiceIntegrationTest {

    @Qualifier("combinationRepository")
    @Autowired
    private CombinationRepository combinationRepository;

    @Qualifier("wordRepository")
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private CombinationService combinationService;

    @BeforeEach
    public void setup() {
        combinationRepository.deleteAll();
        wordRepository.deleteAll();
        combinationService.setupCombinationDatabase();
    }

    @Test
    void getCombination_manyCombinations_success() {
        Combination combo1 = combinationService.getCombination(new Word("fire", 0), new Word("water", 0));
        Combination combo2 = combinationService.getCombination(new Word("fire", 0), new Word("water", 0));
        Combination combo3 = combinationService.getCombination(new Word("fire", 0), new Word("earth", 0));

        assertEquals(combo1, combo2);
        assertNotEquals(combo1, combo3);
    }

    @Test
    void makeCombinations_multipleCombinations_success() {
        int beforeCount = combinationRepository.findAll().size();
        combinationService.makeCombinations(5);
        int afterCount = combinationRepository.findAll().size();

        assertEquals(beforeCount + 5, afterCount);
    }

    @Test
    void generateWordWithinReachability_success() {
        for (double reachability = 0.25; reachability <= 0.625; reachability += 0.125) {
            double minReachability = reachability - 0.125;
            double maxReachability = reachability + 0.125;

            Word word = combinationService.generateWordWithinReachability(minReachability, maxReachability);

            assertTrue(word.getReachability() >= minReachability);
            assertTrue(word.getReachability() <= maxReachability);
        }
    }
}
