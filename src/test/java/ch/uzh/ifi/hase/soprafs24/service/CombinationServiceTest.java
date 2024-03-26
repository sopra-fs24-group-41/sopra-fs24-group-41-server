package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Combination;
import ch.uzh.ifi.hase.soprafs24.entity.Word;
import ch.uzh.ifi.hase.soprafs24.exceptions.CombinationNotFoundException;
import ch.uzh.ifi.hase.soprafs24.repository.CombinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CombinationServiceTest {

    @Mock
    private CombinationRepository combinationRepository;

    @InjectMocks
    private CombinationService combinationService;

    private Combination testCombination;
    private Word testResult;
    private Word testWord1;
    private Word testWord2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testResult = new Word();
        testResult.setName("Steam");
        testWord1 = new Word();
        testWord1.setName("Water");
        testWord2 = new Word();
        testWord2.setName("Fire");

        testCombination = new Combination();
        testCombination.setId(1L);
        testCombination.setResult(testResult);
        testCombination.setWord1(testWord1);
        testCombination.setWord1(testWord2);

        Mockito.when(combinationRepository.save(Mockito.any())).thenReturn(testCombination);
    }

    @Test
    public void findCombination_success() {
        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(testCombination);

        Combination foundCombination = combinationService.findCombination(testWord1, testWord2);

        assertEquals(testCombination.getId(), foundCombination.getId());
        assertEquals(testCombination.getResult(), foundCombination.getResult());
        assertEquals(testCombination.getWord1(), foundCombination.getWord1());
        assertEquals(testCombination.getWord2(), foundCombination.getWord2());
    }

    @Test
    public void findSwappedCombination_success() {
        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, testWord2)).thenReturn(testCombination);

        Combination foundCombination = combinationService.findCombination(testWord2, testWord1);  // swapped words

        assertEquals(testCombination.getId(), foundCombination.getId());
        assertEquals(testCombination.getResult(), foundCombination.getResult());
        assertEquals(testCombination.getWord1(), foundCombination.getWord1());
        assertEquals(testCombination.getWord2(), foundCombination.getWord2());
    }

    @Test
    public void findCombination_throwsException() {
        Word word3 = new Word();
        word3.setName("Rock");

        Mockito.when(combinationRepository.findByWord1AndWord2(testWord1, word3)).thenReturn(null);

        assertThrows(CombinationNotFoundException.class, () -> combinationService.findCombination(testWord1, word3));
    }
}
