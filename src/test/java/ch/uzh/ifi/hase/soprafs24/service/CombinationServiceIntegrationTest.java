package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private WordService wordService;

    @Autowired
//    @Spy
//    @InjectMocks
    private CombinationService combinationService;

    @BeforeEach
    public void setup() {
        combinationRepository.deleteAll();
        wordRepository.deleteAll();
    }

    @Test
    void getCombination_manyCombinations_success() {
        Combination combo1 = combinationService.getCombination(new Word("fire"), new Word("water"));
        Combination combo2 = combinationService.getCombination(new Word("fire"), new Word("water"));
        Combination combo3 = combinationService.getCombination(new Word("fire"), new Word("earth"));

        assertEquals(combo1.getWord1().getName(), combo2.getWord1().getName());
        assertEquals(combo1.getWord2().getName(), combo2.getWord2().getName());
        assertEquals(combo1.getResult().getName(), combo2.getResult().getName());

        assertEquals(combo1.getWord1().getName(), combo3.getWord1().getName());
        assertNotEquals(combo1.getWord2().getName(), combo3.getWord2().getName());
    }

    @Test
    void makeCombinations_multipleCombinations_success() {
        ArrayList<Word> startingWords = new ArrayList<>(Arrays.asList(new Word("water"), new Word("earth"),
                new Word("fire"), new Word("air")));

        for (Word word : startingWords) {
            Word foundWord = wordService.getWord(word);
            foundWord.setDepth(0);
            foundWord.setReachability(1e6);
            wordService.saveWord(foundWord);
        }

        combinationService.makeCombinations(5);

        assertEquals(5, combinationRepository.findAll().size());
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
        wordService.saveWord(volcano);
        wordService.saveWord(earthquake_initial);
        wordService.saveWord(apocalypse_initial);

        combinationService.saveCombination(
                new Combination(wordService.getWord(earthquake_initial),
                wordService.getWord(volcano),
                wordService.getWord(apocalypse_initial)));

        // Execution

        CombinationService spy = Mockito.spy(combinationService);

        Word earthquake_new = new Word("earthquake", 1, 1.0 / (1L << 1));
        Combination firstCombination = new Combination(earth, earth, earthquake_new);

//        Mockito.doReturn(earthquake_new).when(spy).generateCombinationResult(earth, earth);
        Mockito.when(spy.generateCombinationResult(earth, earth)).thenReturn(earthquake_new);

        Word apocalypse_new = new Word("apocalypse", 4, 1.0 / (1L << 4));
        Combination secondCombination = new Combination(earthquake_new, volcano, apocalypse_new);

        spy.createCombination(earth, earth);

        assertEquals(wordService.getWord(earthquake_new), earthquake_new);
        assertEquals(wordService.getWord(apocalypse_new), apocalypse_new);
    }

}
