package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

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

    @Mock
    private APIService apiService;

    @Autowired
    @InjectMocks
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

    @Test
    void propagateWordUpdates_whenResultWordSeenBeforeAndBiggerDepthOnOldCombination_propagatesDepthAndUpdatesReachability() {
        // Assume that we already have the combination "earthquake" + "volcano" == "apocalypse" (depth 5).
        // Then, we discover that "earth" + "earth" == "earthquake" (so, "earthquake" now has depth 1)
        // and now "apocalypse" should also have a smaller depth (depth 4) and its reachability updated.

        Word earth = new Word("earth",0,1e6);
        Word volcano = new Word("volcano", 3, (double) 1 / (1L << 3));
        Word earthquake_initial = new Word("earthquake", 4, (double) 1 / (1L << 4));
        Word apocalypse_initial = new Word("apocalypse", 5, (double) 1 / (1L << 5));

        // Setup

        wordService.saveWord(earth);

        combinationService.saveCombination(
                new Combination(wordService.getWord(earthquake_initial),
                wordService.getWord(volcano),
                wordService.getWord(apocalypse_initial)));

        // Execution

        Word earthquake_new = new Word("earthquake", 1, 1.0 / (1L << 1));
        Combination firstCombination = new Combination(earth, earth, earthquake_new);

        Mockito.when(apiService.generateCombinationResult(earth.getName(), earth.getName())).thenReturn(earthquake_new.getName());

        Word apocalypse_new = new Word("apocalypse", 4, 1.0 / (1L << 4));
        Combination secondCombination = new Combination(earthquake_new, volcano, apocalypse_new);

        combinationService.createCombination(earth, earth);

        assertEquals(wordService.getWord(earthquake_new), earthquake_new);
        assertEquals(wordService.getWord(apocalypse_new), apocalypse_new);
    }

}
